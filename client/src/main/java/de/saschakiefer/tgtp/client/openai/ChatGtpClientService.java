package de.saschakiefer.tgtp.client.openai;

import de.saschakiefer.tgtp.client.openai.model.CompletionDTO;
import de.saschakiefer.tgtp.client.openai.model.CompletionResponseDTO;
import de.saschakiefer.tgtp.client.openai.model.ErrorDTO;
import de.saschakiefer.tgtp.client.openai.model.MessageDTO;
import de.saschakiefer.tgtp.core.exception.client.ChatGtpConnectivityException;
import de.saschakiefer.tgtp.core.model.Message;
import de.saschakiefer.tgtp.core.service.adapter.CoreChatClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class ChatGtpClientService implements CoreChatClient {
    public static final String MODEL = "gpt-3.5-turbo";
    public static final String BASE_URL = "https://api.openai.com/v1";
    public static final String CHAT_COMPLETIONS = "/chat/completions";

    @Value("${tgtp.openai.api.token}")
    private String apiToken;

    @Override
    public Message getMessageForChatHistory(List<Message> history) throws ChatGtpConnectivityException {
        CompletionDTO completion = new CompletionDTO();
        completion.setModel(MODEL);
        completion.setMessages(history.stream()
                .map(message -> new MessageDTO(message.getRole(), message.getContent())).toList());

        WebClient webCLient = WebClient.builder()
                .baseUrl(BASE_URL)
//				.filter(authenticationFilter)
                .build();

        String message = "Could not get answer from ChatGTP Service";
        Mono<CompletionResponseDTO> responseSpec;
        try {
            responseSpec = webCLient
                    .post()
                    .uri(CHAT_COMPLETIONS)
                    .header("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                    .headers(h -> h.setBearerAuth(apiToken))
                    .body(Mono.just(completion), CompletionDTO.class)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, error -> handleError(error, message))
                    .bodyToMono(CompletionResponseDTO.class);
        } catch (WebClientRequestException e) {
            throw new ChatGtpConnectivityException(message, e);
        }

        CompletionResponseDTO response = responseSpec.block();

        assert response != null;
        return new Message(
                response.getChoices().get(0).getMessage().getRole(),
                response.getChoices().get(0).getMessage().getContent());
    }

    private Mono<? extends Throwable> handleError(ClientResponse error, String message) throws ChatGtpConnectivityException {
        return error.bodyToMono(ErrorDTO.class)
                .handle((errorResponse, sink) -> {
                    String errorMsg = message;
                    if (errorResponse != null) {
                        errorMsg = errorMsg + ": " + errorResponse.getError().getMessage();
                    }
                    sink.error(new ChatGtpConnectivityException(errorMsg));
                });
    }
}
