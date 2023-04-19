package de.saschakiefer.tgtp.client.openai.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CompletionDTO {
	private String model;
	private List<MessageDTO> messages;
}
