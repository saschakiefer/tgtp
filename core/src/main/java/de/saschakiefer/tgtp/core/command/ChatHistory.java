package de.saschakiefer.tgtp.core.command;

import de.saschakiefer.tgtp.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@RequiredArgsConstructor
@ShellComponent
public class ChatHistory {
    private final ChatService chatService;

    @ShellMethod(value = "Get the conversation history", key = {"chat-history", "ch"})
    public String chatHistory() {
        return chatService.getFormattedHistory();
    }
}
