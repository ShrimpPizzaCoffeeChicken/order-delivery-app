package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuAndOptionValidRequestDto {
    @JsonProperty("menu-list")
    private List<MenuList> menuList;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuList{
        private String id;
        @JsonProperty("option-list")
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
