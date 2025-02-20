package com.fortest.orderdelivery.app.domain.menu.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuListGetResponseDto {
    private long totalContents;
    private int size;
    private int currentPage;
    private List<MenuListDto> menuList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuListDto {
        private String name;
        private String description;
        private Integer price;
        private String imageUrl;
        private LocalDateTime createAt;
        private LocalDateTime updateAt;
    }
}
