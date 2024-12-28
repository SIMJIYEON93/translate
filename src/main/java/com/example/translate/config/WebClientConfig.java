package com.example.translate.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@Configuration
@PropertySource("classpath:application.properties")
public class WebClientConfig {
    private final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${openrouter.api.key:}")
    private String openrouterApiKey;

    @Value("${openrouter.api.url:}")
    private String openrouterApiUrl;

    @Bean(name = "openRouterWebClient")
    public WebClient webClient() {
        // 환경 변수 유효성 검증
        if (openrouterApiKey == null || openrouterApiKey.isBlank()) {
            logger.error("OpenRouter API Key is not set. Please set OPENROUTER_API_KEY environment variable.");
            throw new IllegalStateException("OpenRouter API Key is not configured");
        }

        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(120))
                        .addHandlerLast(new WriteTimeoutHandler(120)));

        HttpClient httpClient = HttpClient.from(tcpClient)
                .responseTimeout(Duration.ofSeconds(120));

        logger.info("Initializing WebClient with base URL: {}", openrouterApiUrl);

        return WebClient.builder()
                .baseUrl(openrouterApiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Authorization", "Bearer " + openrouterApiKey)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "text/event-stream")
                .filter((request, next) -> {
                    logger.info("Request: {} {}", request.method(), request.url());
                    request.headers().forEach((name, values) ->
                            values.forEach(value -> {
                                if (!name.equalsIgnoreCase("Authorization")) {
                                    logger.info("Header '{}': {}", name, value);
                                }
                            })
                    );
                    return next.exchange(request).doOnNext(response -> {
                        logger.info("Response status code: {}", response.statusCode());
                    });
                })
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .build();
    }
}
