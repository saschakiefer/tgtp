package de.saschakiefer.tgtp.client.openai.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ErrorDTO {
    private ErrorMessageDTO error;
}
