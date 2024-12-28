package com.example.translate;

import com.example.translate.dto.TranslationRequestDto;
import com.example.translate.dto.TranslationResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/translate")
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    public Mono<ResponseEntity<TranslationResponseDto>> translate(@RequestBody @Valid TranslationRequestDto requestDto) {
        return translationService.translateAndSave(requestDto)
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error during translation: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body(
                        new TranslationResponseDto(null, requestDto.getInputText(), "Translation failed due to server error.")
                )));
    }
}

