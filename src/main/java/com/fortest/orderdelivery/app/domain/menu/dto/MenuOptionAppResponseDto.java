package com.fortest.orderdelivery.app.domain.menu.dto;

import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionAppResponseDto {
    private List<MenuOption> menuOptionList;
}
