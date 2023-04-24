package de.saschakiefer.tgtp.terminal.command;

import de.saschakiefer.tgtp.core.exception.client.ChatGtpConnectivityException;
import de.saschakiefer.tgtp.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
@RequiredArgsConstructor
@Slf4j
public class Chat {
    private final ChatService chatService;

    @ShellMethod(value = "Interactively chat with Chat GTP", key = {"chat", "c"})
    public String chat(String input) {
        try {
            return AnsiOutput.toString(
                    AnsiColor.BRIGHT_CYAN,
                    String.format("\n%s\n", chatService.addMessageToChatAndGetResponse(input).getContent()));
        } catch (ChatGtpConnectivityException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
