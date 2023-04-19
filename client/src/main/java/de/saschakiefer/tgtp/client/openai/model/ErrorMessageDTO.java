package de.saschakiefer.tgtp.client.openai.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ErrorMessageDTO {
    private String message;
    private String type;
    private String param;
    private String code;
}
