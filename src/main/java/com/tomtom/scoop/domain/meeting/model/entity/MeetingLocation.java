package com.tomtom.scoop.domain.meeting.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MeetingLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meeting_location_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String city;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String locationName;

    @Column(nullable = false, columnDefinition = "VARCHAR(255)")
    private String locationDetail;

    @Column(nullable = false)
    private Point location;
}
