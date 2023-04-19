package de.saschakiefer.tgtp.client.openai.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ChoiceDTO {
	private MessageDTO message;

	@JsonProperty("finish_reason")
	private String finishRreason;

	private int index;
}
