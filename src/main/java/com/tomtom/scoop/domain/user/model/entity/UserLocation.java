package com.tomtom.scoop.domain.user.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Entity
@Getter
public class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_location_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isDeleted;

    @Column(nullable = false)
    private Point loc;

    @Column(nullable = false, name = "\"range\"")
    private Float range;

    @Column(nullable = false, columnDefinition = "TINYINT")
    private boolean isVerified;

}
