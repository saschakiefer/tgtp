package de.saschakiefer.tgtp.core.command;

import de.saschakiefer.tgtp.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.shell.standard.commands.History;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class ChatHistory implements History.Command {
    private final ChatService chatService;

    @ShellMethod(value = "Get the conversation history", key = {"history", "ch"})
    public String history(@ShellOption(defaultValue = "false") boolean clear) {
        if (clear) {
            chatService.clearHistory();
            log.info("Chat history is cleared");
        }

        return chatService.getFormattedHistory();
    }
}
