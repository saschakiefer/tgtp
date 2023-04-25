package de.saschakiefer.tgtp.core.service.adapter;

import de.saschakiefer.tgtp.core.exception.client.PlaylistCreationException;
import de.saschakiefer.tgtp.core.model.Playlist;
import de.saschakiefer.tgtp.core.model.Song;

import java.util.List;

public interface CoreSpotifyClient {
    Playlist createSpotifyPlaylist(String name, List<Song> playlist) throws PlaylistCreationException;
}
