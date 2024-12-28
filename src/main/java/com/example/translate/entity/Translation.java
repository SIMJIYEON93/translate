package com.example.translate.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
public class Translation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String inputText;

    @Column(name = "translated_text", nullable = false, columnDefinition = "LONGTEXT")
    private String translatedText;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 요청 생성 시간
}