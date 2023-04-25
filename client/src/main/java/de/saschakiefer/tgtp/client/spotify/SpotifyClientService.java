package de.saschakiefer.tgtp.client.spotify;

import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Song;
import de.saschakiefer.tgtp.core.service.adapter.CoreSpotifyClient;
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
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class SpotifyClientService implements CoreSpotifyClient {

    @Value("${tgtp.spotify.clientId}")
    String clientId;

    @Value("${tgtp.spotify.clientSecret}")
    String clientSecret;

    @Value("${tgtp.spotify.refreshToken}")
    String refreshToken;

    @Value("${tgtp.spotify.userId}")
    String userId;

    private SpotifyApi spotifyApi;


    @Override
    public de.saschakiefer.tgtp.core.model.Playlist createSpotifyPlaylist(String name, List<Song> songs) throws PlaylistCreationException {
        initializeSpotifyApi();

        try {
            Playlist playlist = spotifyApi.createPlaylist(userId, name)
                    .build()
                    .execute();

            addSongsToSpotifyPlaylist(playlist, songs);
            return new de.saschakiefer.tgtp.core.model.Playlist(playlist.getName(), playlist.getExternalUrls().get("spotify"));
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new PlaylistCreationException(e.getMessage());
        }
    }


    private void addSongsToSpotifyPlaylist(Playlist spotifyPlaylist, List<Song> playlist) {

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


    private void initializeSpotifyApi() throws PlaylistCreationException {

        try {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(clientId)
                    .setClientSecret(clientSecret)
                    .setRefreshToken(refreshToken)
                    .build();

            AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh()
                    .build();
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new PlaylistCreationException(e.getMessage());
        }
    }
}
