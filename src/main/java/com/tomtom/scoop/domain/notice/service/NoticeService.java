package com.tomtom.scoop.domain.notice.service;

import com.tomtom.scoop.domain.notice.model.dto.NoticeDto;
import com.tomtom.scoop.domain.notice.model.entity.Notice;
import com.tomtom.scoop.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository noticeRepository;


    public List<NoticeDto.response> findAllNotices() {
        List<Notice> notices = noticeRepository.findAll();
        return notices.stream().map(notice -> new NoticeDto.response(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getCreatedAt(),
                notice.getUpdatedAt()
        )).toList();
    }


    public NoticeDto.response findNoticeById(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다. id=" + id));
        return new NoticeDto.response(notice.getId(), notice.getTitle(), notice.getContent(), notice.getCreatedAt(), notice.getUpdatedAt());
    }

    public NoticeDto.response createNotice(NoticeDto.request noticeDto) {
        Notice notice = noticeRepository.save(new Notice(
                noticeDto.getTitle(),
                noticeDto.getContent()
        ));
        return new NoticeDto.response(notice.getId(), notice.getTitle(), notice.getContent(), notice.getCreatedAt(), notice.getUpdatedAt());
    }

    public NoticeDto.response deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다. id=" + id));
        noticeRepository.delete(notice);
        return new NoticeDto.response(notice.getId(), notice.getTitle(), notice.getContent(), notice.getCreatedAt(), notice.getUpdatedAt());
    }


}
