package de.saschakiefer.tgtp.terminal.command;

import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Playlist;
import de.saschakiefer.tgtp.core.service.SpotifyPlaylistService;
import lombok.RequiredArgsConstructor;
import org.jline.utils.Log;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
@RequiredArgsConstructor
public class SpotifyPlaylist {

    private final SpotifyPlaylistService spotifyPlaylistService;

    @ShellMethod(value = "Create a Spotify Playlist", key = {"create-playlist", "cp"})
    public String createPlaylist(String definition, @ShellOption(defaultValue = "t-gtp Playlist") String name) {

        try {
            Playlist playlist = spotifyPlaylistService.createPlaylist(definition, name);
            return AnsiOutput.toString(
                    AnsiColor.BRIGHT_CYAN,
                    String.format("\nI created the Spotify Playlist '%s'. You can open it here: %s",
                            playlist.getName(),
                            playlist.getUrl()));
        } catch (PlaylistCreationException e) {
            Log.error(e.getMessage());
            return null;
        }
    }
}
