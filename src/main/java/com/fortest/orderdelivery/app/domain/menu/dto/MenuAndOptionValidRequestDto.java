package com.fortest.orderdelivery.app.domain.menu.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuAndOptionValidRequestDto {
    private List<MenuList> menuList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuList{
        private String id;
        private List<OptionList> optionList;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class OptionList{
            private String id;
        }
    }
}
