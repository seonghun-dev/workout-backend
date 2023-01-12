package com.tomtom.scoop.domain.group.repository;

import com.tomtom.scoop.domain.group.model.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
