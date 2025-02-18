package com.fortest.orderdelivery.app.domain.order.mapper;

import com.fortest.orderdelivery.app.domain.order.dto.OrderGetListResponseDto;
import com.fortest.orderdelivery.app.domain.order.dto.OrderSaveRequestDto;
import com.fortest.orderdelivery.app.domain.order.dto.StoreMenuValidResponseDto;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOptionMenuOrder;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOrder;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.global.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class OrderMapper {

    /**
     * dto -> order, menuOrder, MenuOptionMenuOrder 가 세팅된 Entity 반환
     * @param saveDto  : 사용자 주문 DTO
     * @param validDto : 가격 정보를 가진 검증 결과 DTO
     * @return order, menuOrder, MenuOptionMenuOrder 가 바인딩 된 Entity 반환
     */
    public static Order saveDtoToEntity(OrderSaveRequestDto saveDto, StoreMenuValidResponseDto validDto, Long userId, String userName) {

        // 가격 정보 세팅
        HashMap<String, StoreMenuValidResponseDto.MenuDto> menuValidMap = new HashMap<>();
        HashMap<String, StoreMenuValidResponseDto.OptionDto> optionValidMap = new HashMap<>();
        for (StoreMenuValidResponseDto.MenuDto menuValidDto : validDto.getMenuList()) {
            menuValidMap.put(menuValidDto.getId(), menuValidDto);
            for (StoreMenuValidResponseDto.OptionDto optionValidDto : menuValidDto.getOptionList()) {
                optionValidMap.put(optionValidDto.getId(), optionValidDto);
            }
        }

        // dto -> entity 시작
        // total price 연산
        int totalPrice = 0;
        List<MenuOrder> menuOrderList = new ArrayList<>();
        for (OrderSaveRequestDto.MenuDto menuSaveDto : saveDto.getMenuList()) {
            StoreMenuValidResponseDto.MenuDto menuValidDto = menuValidMap.get(menuSaveDto.getId());

            List<MenuOptionMenuOrder> menuOptionMenuOrderList = new ArrayList<>();
            MenuOrder menu = MenuOrder.builder()
                    .menuId(menuValidDto.getId())
                    .menuName(menuValidDto.getName())
                    .price(menuValidDto.getPrice())
                    .count(menuSaveDto.getCount())
                    .menuOptionMenuOrderList(menuOptionMenuOrderList)
                    .build();
            menuOrderList.add(menu);

            totalPrice += menuValidDto.getPrice() * menuSaveDto.getCount();

            for (OrderSaveRequestDto.OptionDto optionSaveDto : menuSaveDto.getOptionList()) {
                StoreMenuValidResponseDto.OptionDto optionValidDto = optionValidMap.get(optionSaveDto.getId());

                MenuOptionMenuOrder menuOption = MenuOptionMenuOrder.builder()
                        .menuOptionId(optionValidDto.getId())
                        .menuOptionName(optionValidDto.getName())
                        .menuOptionPrice(optionValidDto.getPrice())
                        .menuOptionCount(optionValidDto.getPrice())
                        .build();
                menuOptionMenuOrderList.add(menuOption);

                totalPrice += optionValidDto.getPrice() * optionSaveDto.getCount();
            }
            menu.addMenuOptionMenuOrderList(menuOptionMenuOrderList);
        }

        Order order = Order.builder()
                .storeId(saveDto.getStoreId())
                .storeName(validDto.getStoreName())
                .totalPrice(totalPrice)
                .customerName(userName)
                .orderStatus(Order.OrderStatus.WAIT)
                .orderType(Order.getOrderTypeByString(saveDto.getOrderType()))
                .menuOrderList(menuOrderList)
                .build();
        order.isCreatedBy(userId);

        return order;
    }

    public static OrderGetListResponseDto pageToGetOrderListDto(Page<Order> page) {
        OrderGetListResponseDto.OrderGetListResponseDtoBuilder builder = OrderGetListResponseDto.builder();
        builder = builder
                .search(null)
                .totalContents(page.getTotalElements())
                .size(page.getSize() + 1)
                .currentPage(page.getNumber() + 1);
        List<OrderGetListResponseDto.OrderDto> orderDtoList = page.getContent().stream()
                .map(OrderMapper::entityToOrderListDtoElement)
                .collect(Collectors.toList());
        builder = builder.orderList(orderDtoList);
        return builder.build();
    }

    public static OrderGetListResponseDto pageToGetOrderListDto(Page<Order> page, String search) {
        OrderGetListResponseDto.OrderGetListResponseDtoBuilder builder = OrderGetListResponseDto.builder();
        builder = builder
                .search(search)
                .totalContents(page.getTotalElements())
                .size(page.getSize() + 1)
                .currentPage(page.getNumber() + 1);
        List<OrderGetListResponseDto.OrderDto> orderDtoList = page.getContent().stream()
                .map(OrderMapper::entityToOrderListDtoElement)
                .collect(Collectors.toList());
        builder = builder.orderList(orderDtoList);
        return builder.build();
    }

    public static OrderGetListResponseDto.OrderDto entityToOrderListDtoElement(Order order) {
        return OrderGetListResponseDto.OrderDto.builder()
                .orderId(order.getId())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .price(order.getTotalPrice())
                .createdAt(CommonUtil.LDTToString(order.getCreatedAt()))
                .updatedAt(CommonUtil.LDTToString(order.getUpdatedAt()))
                .build();
    }


}
