package com.tomtom.scoop.domain.user.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLocationDto {

    @Schema(description = "User Location Country Name", example = "Korea")
    private String county;

    @Schema(description = "User Location City Name", example = "Seoul")
    private String city;

    @Schema(description = "User Location Latitude", example = "37.5419")
    private Float latitude;

    @Schema(description = "User Location Longitude", example = "127.0738")
    private Float longitude;
}
