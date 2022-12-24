package com.tomtom.scoop.domain.notice.controller;

import com.tomtom.scoop.domain.notice.model.dto.NoticeDto;
import com.tomtom.scoop.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @ResponseBody
    public List<NoticeDto.response> findAllNotices() {
        return noticeService.findAllNotices();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public NoticeDto.response findNoticeById(@PathVariable("id") Long id) {
        return noticeService.findNoticeById(id);
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public NoticeDto.response createNotice(@RequestBody NoticeDto.request noticeDto) {
        return noticeService.createNotice(noticeDto);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public NoticeDto.response deleteNotice(@PathVariable("id") Long id) {
        return noticeService.deleteNotice(id);
    }


}
