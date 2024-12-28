package com.example.translate;


import com.example.translate.aiapi.TranslationRequest;
import com.example.translate.dto.TranslationRequestDto;
import com.example.translate.dto.TranslationResponseDto;
import com.example.translate.entity.Translation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranslationService {

    private final TranslationRepository repository;
    private final WebClient webClient;

    @Value("${openrouter.api.url}")
    private String openRouterApiUrl;

    public Mono<TranslationResponseDto> translateAndSave(TranslationRequestDto requestDto) {
        return callOpenAiApi(requestDto.getInputText())
                .flatMap(translatedText -> {
                    Translation translation = new Translation();
                    translation.setInputText(requestDto.getInputText());
                    translation.setTranslatedText(translatedText);
                    translation.setCreatedAt(LocalDateTime.now());

                    return Mono.fromCallable(() -> repository.save(translation))
                            .subscribeOn(Schedulers.boundedElastic())
                            .map(savedTranslation -> new TranslationResponseDto(
                                    savedTranslation.getId(),
                                    savedTranslation.getInputText(),
                                    savedTranslation.getTranslatedText()
                            ));
                })
                .doOnError(e -> log.error("Translation failed: {}", e.getMessage()))
                .onErrorReturn(new TranslationResponseDto(null, requestDto.getInputText(), "Translation failed due to server error."));
    }

    private Mono<String> callOpenAiApi(String inputText) {
        TranslationRequest request = new TranslationRequest(inputText, 0.7, 1.0);

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/chat/completions")
                        .build())
                .bodyValue(request.toApiRequest())
                .retrieve()
                .toEntity(String.class) // 응답을 String으로 받아서 디버깅
                .doOnNext(response -> log.info("Raw response: {}", response.getBody()))
                .doOnNext(response -> log.info("Response Headers: {}", response.getHeaders()))
                .flatMap(response -> {
                    if (response.getHeaders().getContentType() != null &&
                            response.getHeaders().getContentType().toString().equals("application/json")) {
                        return Mono.just(response.getBody());
                    } else {
                        log.error("Unexpected content type: {}", response.getHeaders().getContentType());
                        return Mono.error(new RuntimeException("Unsupported content type"));
                    }
                })
                .onErrorMap(WebClientResponseException.class, e -> {
                    log.error("API Error: Status {}, Body {}", e.getStatusCode(), e.getResponseBodyAsString());
                    return new RuntimeException("Error from OpenAI API: " + e.getMessage(), e);
                });
    }
}
