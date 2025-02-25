package com.fortest.orderdelivery.app.domain.order.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderGetListRequestDto {
    @Positive(message = "page 값은 음수일 수 없습니다.")
    private Integer page;
    @Positive(message = "page 값은 음수일 수 없습니다.")
    private Integer size;
    @Pattern(regexp = "CREATED|UPDATED", message = "orderby 는 CREATED, UPDATED 만 허용합니다.")
    private String orderby;

    private String sort;
    @Size(max = 100)
    private String search;
}
