package com.tomtom.scoop.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_location_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String county;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String city;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String dong;

}
