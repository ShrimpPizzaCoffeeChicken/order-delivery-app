package com.fortest.orderdelivery.app.domain.menu.service;

import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidRequestDto.MenuList.OptionList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAndOptionValidResponseDto.MenuList;
import com.fortest.orderdelivery.app.domain.menu.dto.MenuAppResponseDto;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.mapper.MenuMapper;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuOptionQueryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuAppService {

    private final MenuService menuService;
    private final MenuOptionQueryRepository menuOptionQueryRepository;

    public MenuAppResponseDto getMenuFromApp(List<String> menuId) {
        List<Menu> menuList = new ArrayList<>();

        menuId.forEach(id -> {
            Menu menu = menuService.getMenuById(id);
            menuList.add(menu);
        });
        return MenuMapper.toMenuAppResponseDto(menuList);
    }

    public MenuAndOptionValidResponseDto validateMenuAndOption(MenuAndOptionValidRequestDto menuAndOptionValidRequestDto) {
        List<MenuAndOptionValidResponseDto.MenuList> resultMenuList = new ArrayList<>();
        Boolean result = true;

        //request menuList 없는 경우 함수 return
        if (Objects.isNull(menuAndOptionValidRequestDto.getMenuList())) {
            return null;
        }

        //List에 있는 메뉴들을 하나씩 꺼내서 for문 돌기
        for (MenuAndOptionValidRequestDto.MenuList menu : menuAndOptionValidRequestDto.getMenuList()) {
            //하나의 메뉴
            String menuId = menu.getId();
            Menu savedMenu = null;

            //해당 메뉴에 대한 옵션 id들을 받아와서 검증 후 option 객체 획득
            List<MenuOption> menuOptionList = menuOptionQueryRepository.getMenuOptionsAndMenusByMenuOptionIds(
                menu.getOptionList().stream()
                    .map(OptionList::getId)
                    .toList()
            );

            //responseOptionList 생성
            List<MenuAndOptionValidResponseDto.MenuList.OptionList> resultOptionList = new ArrayList<>();

            //만약 획득된 option 객체가 없다면 (삭제된 option의 id 였다거나 ) resultOptionList를 null로 두어 아래에서 따로 처리할 수 있도록 함 (1)
            if(Objects.isNull(menuOptionList) || menuOptionList.isEmpty()) {
                savedMenu = menuService.getMenuById(menuId);
                resultOptionList = null;
            } else { //제대로 option 객체들이 획득 되었다면 List에서 option을 하나씩 꺼내서 for문 돌기 (2)
                for (MenuOption option : menuOptionList) {
                    //request에서 받은 option의 메뉴 id와 option table에 매핑되어있는 메뉴 id가 다르다면 (3)
                    if (!Objects.equals(menuId, option.getMenu().getId())) {
                        result = false; // 실패 처리
                        break;
                    }

                    //일치하다면 response 용 optionList 생성
                    resultOptionList.add(MenuAndOptionValidResponseDto.MenuList.OptionList.builder()
                        .id(option.getId())
                        .price(option.getPrice())
                        .name(option.getName())
                        .build());
                }
            }

            //(2)의 경우라면 optionList를 menuList에 매핑 (response용)
            if(result && !Objects.isNull(resultOptionList)) {
                resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                    .id(menuId)
                    .price(menuOptionList.get(0).getMenu().getPrice())
                    .name(menuOptionList.get(0).getMenu().getName())
                    .optionList(resultOptionList)
                    .build());
            } else if (result) {//(1)의 경우라면 null을 menuList의 optionList에 매핑 (response용)
                resultMenuList.add(MenuAndOptionValidResponseDto.MenuList.builder()
                    .id(menuId)
                    .price(savedMenu.getPrice())
                    .name(savedMenu.getName())
                    .optionList(null)
                    .build());
            } else { //(3)의 경우라면 for문 최상단을 빠져나가서 return 되도록
                break;
            }
        }

        return MenuMapper.toMenuAndOptionValidResponseDto(resultMenuList, result);

    }
}
