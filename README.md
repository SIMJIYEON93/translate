## Translate

이 프로젝트는 OpenRouter을 사용해서 다양한 aiapi(claude-3.5)를 호출 할수 있도록 만든 프로젝트 입니다.

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot)
![Gradle](https://img.shields.io/badge/Gradle-7.x-02303A?style=for-the-badge&logo=gradle)



## 주요 기능

- OpenRouter 사용하여 사용자가 지정한 aiapi 호출하여 mono방식과 flux방식의 응답속도 차이 테스트

## 디렉토리 구조

```
─com
    └─example
        └─aiapi
            ├─aiapi
            └─config
```


### 디렉토리 설명

- `config`: OpenAi서버의 CilentConfig와 SecurityConfig 연결 설정
- `aiapi`: prompt와 max_token으로 mono방식과 flux방식의 응답속도 차이 테스트 API




### 빌드 및 실행 환경
- JDK 17 이상
- Gradle 7.x 이상


### Postman 요청 예시

| **필드**       | **값**                                           |
|----------------|-------------------------------------------------|
| **Method**     | `POST`                                          |
| **URL**        | `http://localhost:8080/api/v1/openai/translate` |
| **Headers**    | `Content-Type: application/json`                |
|                | `Authorization: Bearer <OPENROUTER_API_KEY>`    |
| **Body (JSON)**|                                                 |
|                | {                                               |
|                | "prompt": "양자 물리학의 기본 개념을 설명해 주세요.",            |
|                | "temperature": 1.0,                             |
|                | "topP": 0.9                                     |
|                | }                                               |

```
Method: POST
URL: https://api.openrouter.ai/v1/chat/completions
Headers:
  - Content-Type: application/json
  - Authorization: Bearer <YOUR_OPENROUTER_API_KEY>
Body:
{
  "model": "anthropic.claude-v3.5",
  "messages": [
    { "role": "user", "content": "다음 글을 초등학생이 이해할 수 있도록 쉽게 바꿔줘:\n
    양자 물리학의 기본 개념을 설명해 주세요." }
  ],
  "temperature": 1.0,
  "topP": 0.9
}
```

단, 스트리밍 응답 테스트 할때는 "Send and Download" 또는 "Raw Data Preview" 옵션을 사용해야 합니다


---

## 테스트 결과

**기준 번안글**: [https://www.yna.co.kr/view/AKR20241203051751004?section=search](https://www.yna.co.kr/view/AKR20241203051751004?section=search)  
기준 번안글은 띄어쓰기 포함 **1,162자**입니다.

### **첫번째 데이터 도착 시간**
- **스트림**: 2.599초
- **모노**: 9.269

대략적으로 모노형식은 스트림의 4배 정도의 시간 차이가 납니다.


### **처리 속도**
- **스트림**: 9.556초
- **모노**: 9.745초

---

![Stream 첫번째 데이터 도착시간](./images/stream.png)
![Stream First request time](./images/streamrequesttime.png)
![Stream First response time](./images/streamresponsetime.png)
![Mono Time to first byte](./images/mono.png)
![Mono First request time](./images/monorequesttime.png)
![번안 결과](./images/result.png)
