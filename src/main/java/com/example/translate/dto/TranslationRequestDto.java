package com.example.translate.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TranslationRequestDto {

    @Schema(description = "Input text to be translated", example = "Hello, world!", required = true)
    @NotBlank(message = "Input text cannot be blank.")
    @Size(max = 1200, message = "Input text must not exceed 1200 characters.")
    private String inputText;
}