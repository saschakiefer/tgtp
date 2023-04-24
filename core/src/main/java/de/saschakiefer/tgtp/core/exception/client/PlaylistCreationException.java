package de.saschakiefer.tgtp.core.exception.client;

public class PlaylistCreationException extends RuntimeException {
    public PlaylistCreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaylistCreationException(String message) {
        super(message);
    }
}
