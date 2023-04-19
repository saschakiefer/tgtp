package de.saschakiefer.tgtp.core.service.adapter;

import de.saschakiefer.tgtp.core.exception.client.ChatGtpConnectivityException;
import de.saschakiefer.tgtp.core.model.Message;

import java.util.List;

public interface CoreChatClient {
    Message getMessageForChatHistory(List<Message> history) throws ChatGtpConnectivityException;
}
