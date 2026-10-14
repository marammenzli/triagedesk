package com.triagedesk.ticket_service.ticket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Component
public class AiTriageClient {

    private static final Logger log = LoggerFactory.getLogger(AiTriageClient.class);

    // Field names match the JSON sent by the Python service (suggested_reply has an underscore)
    public record TriageResult(String priority, String category, String suggested_reply) {
    }

    private final RestClient restClient;

    public AiTriageClient(@Value("${ai.service.url}") String baseUrl) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    // Returns empty if the AI service is down or slow: the ticket must still be created
    public Optional<TriageResult> triage(String title, String description) {
        try {
            TriageResult result = restClient.post()
                    .uri("/triage")
                    .body(Map.of("title", title, "description", description))
                    .retrieve()
                    .body(TriageResult.class);
            return Optional.ofNullable(result);
        } catch (Exception e) {
            log.warn("AI triage unavailable, creating ticket without it: {}", e.getMessage());
            return Optional.empty();
        }
    }
}