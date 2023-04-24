package de.saschakiefer.tgtp.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@ComponentScan({"de.saschakiefer.tgtp"})
@Configuration
@PropertySource(value = "file:${user.home}/.config/tgtp.conf")
@Getter
@Setter
@EnableAutoConfiguration
public class TestConfig {
}
