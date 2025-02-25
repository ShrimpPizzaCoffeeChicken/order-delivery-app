package com.fortest.orderdelivery.app.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRollResponseDto {
    @JsonProperty("from-roll")
    private String fromRoll;
    @JsonProperty("to-roll")
    private String toRoll;
}
