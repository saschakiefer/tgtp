package de.saschakiefer.tgtp.core.command;

import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Song;
import de.saschakiefer.tgtp.core.service.SpotifyPlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.List;
import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class SpotifyPlaylist {

    private final SpotifyPlaylistService spotifyPlaylistService;

    @ShellMethod(value = "Create a Spotify Playlist", key = {"create-playlist", "cp"})
    public String createPlaylist(String definition, String name) {
        List<Song> playlist;
        try {
            playlist = spotifyPlaylistService.createPlaylist(definition, name);
        } catch (PlaylistCreationException e) {
            return e.getMessage();
        }

        return "The playlist '" + name + "' was created with the following titles:\n\n" +
                playlistToString(playlist);
    }

    private String playlistToString(List<Song> playlist) {
        return playlist.stream()
                .map(song -> String.format("'%s' by '%s' from album '%s'", song.getTitle(), song.getArtist(), song.getAlbum()))
                .collect(Collectors.joining("\n"));
    }
}
