package de.saschakiefer.tgtp.core.service;

import de.saschakiefer.tgtp.core.exception.client.ChatGtpConnectivityException;
import de.saschakiefer.tgtp.core.model.Message;
import de.saschakiefer.tgtp.core.service.adapter.CoreChatClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ChatService {
    @Getter
    private final List<Message> messages = new ArrayList<>();

    private final CoreChatClient chatClient;

    public ChatService(CoreChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public Message addMessageToChatAndGetResponse(String input) throws ChatGtpConnectivityException {
        if (messages.size() == 0) {
            initializeChat();
        }

        messages.add(new Message("user", input));
        log.info("Sending your request to Chat-GTP");
        Message response = chatClient.getMessageForChatHistory(messages);
        messages.add(response);
        return response;
    }

    public void clearHistory() {
        messages.clear();
    }

    private void initializeChat() {
        messages.clear();
        messages.add(new Message(
                "system",
                "You are T-GPT, a fork of ChatGPT created by Sascha Kiefer. You have all the capabilities of ChatGPT but you run inside an interactive terminal application. " +
                        "Your responses should be informative and clear, but not excessively long. You have to help users quickly. " +
                        "Whenever possible, try to be concise. Never go longer unless I ask you to be more detailed. " +
                        "Current date and time is " +
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
    }
}
