package de.saschakiefer.tgtp.core.service;

import de.saschakiefer.tgtp.core.exception.client.ChatGtpConnectivityException;
import de.saschakiefer.tgtp.core.model.Message;
import de.saschakiefer.tgtp.core.service.adapter.CoreChatClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ChatService {
    @Getter
    private final List<Message> messages = new ArrayList<>();

    @Autowired
    private CoreChatClient chatClient;

    public String addMessageToChatAndGetResult(String input) {
        if (messages.size() == 0) {
            initializeChat();
        }

        messages.add(new Message("user", input));
        Message response;
        try {
            response = chatClient.getMessageForChatHistory(messages);
        } catch (ChatGtpConnectivityException e) {
            return e.getMessage();
        }
        messages.add(response);
        return response.getContent();
    }

    public void initializeChat() {
        messages.clear();
        messages.add(new Message(
                "system",
                "You are T-GPT, a fork of ChatGPT created by Sascha Kiefer. You have all the capabilities of ChatGPT but you run inside an interactive terminal application. " +
                        "Your responses should be informative and clear, but not excessively long. You have to help users quickly. " +
                        "Whenever possible, try to be concise. Never go longer unless I ask you to be more detailed. " +
                        "Current date and time is " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
    }

    public String getFormattedHistory() {
        return messages.stream()
                .map(m -> String.format("%9s: %s", m.getRole(), m.getContent()))
                .collect(Collectors.joining("\n"));
    }

    public void clearHistory() {
        messages.clear();
    }
}
