package com.tomtom.scoop.global.telegram;

import com.tomtom.scoop.infrastructor.telegram.TelegramService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class TelegramProvider {

    private final TelegramService telegramService;

    public void sendError(String method, String url, Exception e) {
        String title = "Internal Server Error\n*요청주소:*\n [" + method + "] " + url;
        String content = "\n*에러내용:*\n" + e.getMessage();
        String time = "\n*발생일시:*" + LocalDateTime.now();
        telegramService.sendMessage(title + content + time);
    }


}
