package com.tomtom.scoop.infrastructor.telegram;

import com.tomtom.scoop.global.exception.BusinessException;
import com.tomtom.scoop.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
public class TelegramService {
    @Value("${telegram.botToken}")
    private String TELEGRAM_BOT_TOKEN;

    @Value("${telegram.chatId}")
    private String TELEGRAM_CHAT_ID;

    public void sendMessage(String message) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://api.telegram.org/bot" + TELEGRAM_BOT_TOKEN + "/sendMessage";
        JSONObject json = new JSONObject();
        json.put("parse_mode", "markdown");
        json.put("chat_id", TELEGRAM_CHAT_ID);
        json.put("text", message);
        String telegramBody = json.toString();
        HttpEntity<String> request = new HttpEntity<>(telegramBody, httpHeaders);
        log.info(request.toString());
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Failed to send message to telegram: " + response.getBody());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}

