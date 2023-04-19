package de.saschakiefer.tgtp.core.exception.client;

public class ChatGtpConnectivityException extends RuntimeException {
	public ChatGtpConnectivityException(String message, Throwable cause) {
		super(message, cause);
	}

	public ChatGtpConnectivityException(String message) {
		super(message);
	}
}
