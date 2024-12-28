package com.example.translate.aiapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class TranslationRequest {

    private String prompt;
    private Double temperature; // 다양성 조절
    private Double topP;        // 상위 확률 조절
    private static final Logger logger = LoggerFactory.getLogger(TranslationRequest.class);

    public Map<String, Object> toApiRequest() {
        if (prompt == null || prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt must not be null or empty.");
        }

        // 프롬프트 수정
        String modifiedPrompt = "다음 내용을 초등학교 3학년이 이해할 수 있도록 쉽게 바꿔주세요:\n" + prompt;

        // 요청 데이터를 순서 보장
        Map<String, Object> request = new LinkedHashMap<>();

        request.put("model", "anthropic/claude-3.5-haiku-20241022");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(new LinkedHashMap<>() {{
            put("role", "user");
            put("content", modifiedPrompt);
        }});
        request.put("messages", messages);

        // 추가 필드
        if (temperature != null) {
            request.put("temperature", temperature);
        }
        if (topP != null) {
            request.put("top_p", topP);
        }

        logger.info("toApiRequest() generated payload: {}", request);

        return request;
    }
}
