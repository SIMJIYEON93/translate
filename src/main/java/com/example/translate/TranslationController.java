package com.example.translate;

import com.example.translate.dto.TranslationRequestDto;
import com.example.translate.dto.TranslationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Translation API", description = "Provides endpoints to translate text")
public class TranslationController {

    private final TranslationService translationService;

    @PostMapping
    @Operation(
            summary = "Translate Text",
            description = "This endpoint accepts input text, translates it, and returns the translation result."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translation completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input text"),
            @ApiResponse(responseCode = "500", description = "Server error occurred")
    })
    public Mono<ResponseEntity<TranslationResponseDto>> translate(@RequestBody @Valid TranslationRequestDto requestDto) {
        return translationService.translateAndSave(requestDto)
                .map(ResponseEntity::ok)
                .doOnError(e -> log.error("Error during translation: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(500).body(
                        new TranslationResponseDto(null, requestDto.getInputText(), "Translation failed due to server error.")
                )));
    }
}

