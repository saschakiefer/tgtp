package de.saschakiefer.tgtp.core.service;

import de.saschakiefer.tgtp.core.exception.client.ChatGtpConnectivityException;
import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Playlist;
import de.saschakiefer.tgtp.core.model.Song;
import de.saschakiefer.tgtp.core.service.adapter.CoreSpotifyClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SpotifyPlaylistService {

    private static final String regex = ".+%%SEPARATOR%%.+";

    private final ChatService chatService;
    private final CoreSpotifyClient spotifyClient;


    public Playlist createPlaylist(String definition, String name) throws PlaylistCreationException {

        String gtpPlaylist = "";
        List<Song> songs;
        try {
            gtpPlaylist = getPlaylistFromChatGtp(definition);
            songs = Arrays.stream(gtpPlaylist.split("\n"))
                    .map(this::getSong)
                    .toList();
            log.debug(songs.stream().map(Song::toString).collect(Collectors.joining(", ")));
        } catch (RuntimeException e) {
            throw new PlaylistCreationException("Could not parse the response from Chat GTP: " + gtpPlaylist);
        }

        return spotifyClient.createSpotifyPlaylist(name, songs);
    }


    private String getPlaylistFromChatGtp(String definition) throws ChatGtpConnectivityException {

        String gtpResponse = chatService.addMessageToChatAndGetResponse(generateRequest(definition)).getContent();

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(gtpResponse);

        // Only get the table from the response
        StringBuilder gtpPlaylist = new StringBuilder();
        while (matcher.find()) {
            gtpPlaylist.append(matcher.group(0)).append("\n");
        }

        log.debug(String.valueOf(gtpPlaylist));
        return gtpPlaylist.toString();
    }


    private Song getSong(String line) {

        String[] lineParts = line.split(" %%SEPARATOR%% ");
        return new Song(lineParts[0], lineParts[1], lineParts[2]);
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
}
