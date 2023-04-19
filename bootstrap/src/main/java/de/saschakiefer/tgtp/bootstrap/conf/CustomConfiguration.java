package de.saschakiefer.tgtp.bootstrap.conf;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:${user.home}/.config/tgtp.conf")
@Getter
@Setter
@Slf4j
public class CustomConfiguration {

    @Value("${tgtp.openai.api.token}")
    private String chatGtpApiToken;
}
