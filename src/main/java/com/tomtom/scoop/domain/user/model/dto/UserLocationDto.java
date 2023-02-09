package com.tomtom.scoop.domain.user.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLocationDto {

    private String county;
    private String city;
    private String dong;

}
