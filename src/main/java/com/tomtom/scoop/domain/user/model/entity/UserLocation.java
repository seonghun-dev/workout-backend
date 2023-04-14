package com.tomtom.scoop.domain.user.model.entity;

import com.tomtom.scoop.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_location_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String county;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String city;

    @Column(nullable = false)
    private Point location;

    @Column(nullable = false)
    private Integer range;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isVerified;

    @Column(nullable = false)
    private LocalDateTime verifiedDate;

    @OneToOne
    private User user;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isDeleted;

}
