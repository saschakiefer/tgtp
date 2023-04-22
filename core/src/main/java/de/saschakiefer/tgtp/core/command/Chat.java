package de.saschakiefer.tgtp.core.command;

import de.saschakiefer.tgtp.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
public class Chat {
    private final ChatService chatService;

    @ShellMethod(value = "Interactively chat with Chat GTP", key = {"chat", "c"})
    public String chat(String input) {
        return chatService.addMessageToChatAndGetResult(input);
    }
}
