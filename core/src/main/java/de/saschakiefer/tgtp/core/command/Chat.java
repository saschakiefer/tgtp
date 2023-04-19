package de.saschakiefer.tgtp.core.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import de.saschakiefer.tgtp.core.service.ChatService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ShellComponent
public class Chat {
	@Autowired
	ChatService chatService;

	@ShellMethod("Interactively chat with Chat GTP")
	public String chat(String input) {
		return chatService.addMessageToChatAndGetResult(input);
	}
}
