package de.saschakiefer.tgtp.core.service;

import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Song;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Playlist;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyPlaylistService {

    private static final String regex = ".+%%SEPARATOR%%.+";

    private final ChatService chatService;

    @Value("${tgtp.spotify.clientId}")
    private String spotifyClientId;

    @Value("${tgtp.spotify.clientSecret}")
    private String spotifyClientSecret;

    @Value("${tgtp.spotify.refreshToken}")
    private String spotifyRefreshToken;

    @Value("${tgtp.spotify.userId}")
    private String spotifyUserId;

    private SpotifyApi spotifyApi;

    public List<Song> createPlaylist(String definition, String name) throws PlaylistCreationException {
        try {
            initializeSpotifyApi();

            StringBuilder gtpPlaylist = getPlaylistFromChatGtp(definition);

            List<Song> playlist = Arrays.stream(gtpPlaylist.toString().split("\n"))
                    .map(this::getSong)
                    .collect(Collectors.toList());

            log.debug(playlist.stream().map(Song::toString).collect(Collectors.joining(", ")));

            createSpotifyPlaylist(name, playlist);

            return playlist;
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new PlaylistCreationException(e.getMessage());
        } catch (RuntimeException e) {
            throw new PlaylistCreationException(
                    chatService.getMessages().get(chatService.getMessages().size() - 1).getContent(), e);
        }
    }

    private void initializeSpotifyApi() throws IOException, ParseException, SpotifyWebApiException {

        spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyClientId)
                .setClientSecret(spotifyClientSecret)
                .setRefreshToken(spotifyRefreshToken)
                .build();

        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                .build();
        AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

        spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
    }

    private Song getSong(String line) {
        String[] lineParts = line.split(" %%SEPARATOR%% ");
        return new Song(lineParts[0], lineParts[1], lineParts[2]);
    }

    private Playlist createSpotifyPlaylist(String name, List<Song> playlist) throws IOException, SpotifyWebApiException, ParseException {
        Playlist spotifyPlaylist = spotifyApi.createPlaylist(spotifyUserId, name)
                .build()
                .execute();

        log.info("Created playlist '{}' ({})", spotifyPlaylist.getName(), spotifyPlaylist.getExternalUrls().get("spotify"));

        addToSpotifyPlaylist(spotifyPlaylist, playlist);

        return spotifyPlaylist;
    }

    private StringBuilder getPlaylistFromChatGtp(String definition) {
        String gtpResponse = chatService.addMessageToChatAndGetResponse(generateRequest(definition)).getContent();

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(gtpResponse);

        // Only get the table from the response
        StringBuilder gtpPlaylist = new StringBuilder();
        while (matcher.find()) {
            gtpPlaylist.append(matcher.group(0)).append("\n");
        }

        log.debug(String.valueOf(gtpPlaylist));
        return gtpPlaylist;
    }

    private void addToSpotifyPlaylist(Playlist spotifyPlaylist, List<Song> playlist) {
        String[] uris = playlist.stream()
                .map(this::searchSpotifySong)
                .filter(Objects::nonNull)
                .toArray(String[]::new);

        try {
            spotifyApi.addItemsToPlaylist(spotifyPlaylist.getId(), uris).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error(e.getMessage());
        }
    }

    private String generateRequest(String playlistDefinition) {
        String fullMessage = """
                Generate a playlist with the following criteria:
                %%PLACEHOLDER%%
                These songs must be real and not a product of your imagination.
                The playlist should be a list of text with each line in this format: Artist Name %%SEPARATOR%% Song Name %%SEPARATOR%% Album
                That's the name of the artist for the song, followed by a space, followed by %%SEPARATOR%%, followed by a space, followed by the name of the song, followed by a space, followed by %%SEPARATOR%%, followed by a space, followed by the name of the album. Follow exactly this definition.
                Use newline characters to separate each line.
                I specifically ask, that the list format follows this definition.
                I specifically ask, that the response only contains the plain list of text with no additional sentences, no notes, no introduction message. Nothing before the list and nothing after the list. Just the list.""";

        return fullMessage.replace("%%PLACEHOLDER%%", playlistDefinition);
    }

    private String searchSpotifySong(Song song) {
        try {
            Paging<Track> trackPaging = spotifyApi.searchTracks(
                            URLEncoder.encode(
                                    String.format("title:%s album:%s artist:%s", song.getTitle(), song.getAlbum(), song.getArtist()),
                                    StandardCharsets.UTF_8))
                    .limit(1)
                    .build()
                    .execute();

            return trackPaging.getItems()[0].getUri();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
