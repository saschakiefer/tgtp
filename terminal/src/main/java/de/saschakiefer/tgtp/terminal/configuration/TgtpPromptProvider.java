package de.saschakiefer.tgtp.terminal.configuration;

import de.saschakiefer.tgtp.core.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TgtpPromptProvider implements PromptProvider {
    private final ChatService chatService;

    @Override
    public AttributedString getPrompt() {
        return new AttributedString(String.format("t-gtp (%d):> ", chatService.getMessages().size()));
    }
}
