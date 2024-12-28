package com.example.translate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TranslationRequestDto {

    @NotBlank(message = "Input text cannot be blank.")
    @Size(max = 1200, message = "Input text must not exceed 1200 characters.")
    private String inputText;
}