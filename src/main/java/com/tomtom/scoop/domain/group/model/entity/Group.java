package com.tomtom.scoop.domain.group.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Group extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(nullable = false)
    private Integer memberLimit;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String explain;

    @Column(columnDefinition = "VARCHAR(255)")
    private String imgUrl;

    @Column(nullable = false)
    private Date eventDate;

    @Column(nullable = false)
    private Integer viewCount;

}
