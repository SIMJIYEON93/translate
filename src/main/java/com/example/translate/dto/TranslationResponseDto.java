package com.example.translate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationResponseDto {
    @Schema(description = "Unique ID of the translation record", example = "1")
    private Long id;

    @Schema(description = "Original input text", example = "Hello, world!")
    private String inputText;

    @Schema(description = "Translated text", example = "안녕하세요, 세상!")
    private String translatedText;
}
