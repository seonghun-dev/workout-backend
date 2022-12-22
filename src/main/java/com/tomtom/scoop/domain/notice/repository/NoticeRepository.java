package com.tomtom.scoop.domain.notice.repository;

import com.tomtom.scoop.domain.notice.model.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
