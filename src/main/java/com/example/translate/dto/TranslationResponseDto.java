package com.example.translate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationResponseDto {
    private Long id;
    private String inputText;
    private String translatedText;
}
