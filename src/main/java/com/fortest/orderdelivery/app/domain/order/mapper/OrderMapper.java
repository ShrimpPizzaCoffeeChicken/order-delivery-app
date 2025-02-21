package com.fortest.orderdelivery.app.domain.order.mapper;

import com.fortest.orderdelivery.app.domain.order.dto.*;
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

        Order order = Order.builder()
                .storeId(saveDto.getStoreId())
                .storeName(validDto.getStoreName())
                .customerName(userName)
                .orderStatus(Order.OrderStatus.WAIT)
                .orderType(Order.getOrderTypeByString(saveDto.getOrderType()))
                .menuOrderList(new ArrayList<>())
                .build();
        order.isCreatedBy(userId);

        int totalPrice = 0;
        for (OrderSaveRequestDto.MenuDto menuSaveDto : saveDto.getMenuList()) {
            StoreMenuValidResponseDto.MenuDto menuOrderValidDto = menuValidMap.get(menuSaveDto.getId());

            MenuOrder menuOrder = MenuOrder.builder()
                    .menuId(menuOrderValidDto.getId())
                    .menuName(menuOrderValidDto.getName())
                    .price(menuOrderValidDto.getPrice())
                    .count(menuSaveDto.getCount())
                    .menuOptionMenuOrderList(new ArrayList<>())
                    .build();
            order.addMenuOrder(menuOrder);

            totalPrice += menuOrderValidDto.getPrice() * menuSaveDto.getCount();

            for (OrderSaveRequestDto.OptionDto optionSaveDto : menuSaveDto.getOptionList()) {
                StoreMenuValidResponseDto.OptionDto optionValidDto = optionValidMap.get(optionSaveDto.getId());

                MenuOptionMenuOrder menuOptionMenuOrder = MenuOptionMenuOrder.builder()
                        .menuOptionId(optionValidDto.getId())
                        .menuOptionName(optionValidDto.getName())
                        .menuOptionPrice(optionValidDto.getPrice())
                        .menuOptionCount(optionValidDto.getPrice())
                        .build();
                menuOrder.addMenuOptionMenuOrder(menuOptionMenuOrder);

                totalPrice += optionValidDto.getPrice() * optionSaveDto.getCount();
            }
        }
        order.updateTotalPrice(totalPrice);

        return order;
    }

    public static OrderGetListResponseDto pageToGetOrderListDto(Page<Order> page, String search) {
        OrderGetListResponseDto.OrderGetListResponseDtoBuilder builder = OrderGetListResponseDto.builder();
        builder = builder
                .search(search == null ? "" : search)
                .totalContents(page.getTotalElements())
                .size(page.getSize())
                .currentPage(page.getNumber() + 1);
        List<OrderGetListResponseDto.OrderDto> orderDtoList = page.getContent().stream()
                .map(OrderMapper::entityToOrderListDtoElement)
                .collect(Collectors.toList());
        builder = builder.orderList(orderDtoList);
        return builder.build();
    }

    private static OrderGetListResponseDto.OrderDto entityToOrderListDtoElement(Order order) {
        return OrderGetListResponseDto.OrderDto.builder()
                .orderId(order.getId())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .price(order.getTotalPrice())
                .createdAt(CommonUtil.LDTToString(order.getCreatedAt()))
                .updatedAt(CommonUtil.LDTToString(order.getUpdatedAt()))
                .build();
    }

    public static OrderGetDetailResponseDto entityToGetDetailDto(Order order) {
        OrderGetDetailResponseDto.OrderGetDetailResponseDtoBuilder orderDtoBuilder = OrderGetDetailResponseDto.builder()
                .orderId(order.getId())
                .createdAt(CommonUtil.LDTToString(order.getCreatedAt()))
                .updatedAt(CommonUtil.LDTToString(order.getUpdatedAt()))
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .price(order.getTotalPrice());

        List<MenuOrder> menuOrderList = order.getMenuOrderList();
        ArrayList<OrderGetDetailResponseDto.MenuDto> menuDtos = new ArrayList<>();
        for (MenuOrder menuOrder : menuOrderList) {

            OrderGetDetailResponseDto.MenuDto.MenuDtoBuilder menuDtoBuilder = OrderGetDetailResponseDto.MenuDto.builder()
                    .menuName(menuOrder.getMenuName())
                    .menuCount(menuOrder.getCount());

            List<MenuOptionMenuOrder> menuOptionMenuOrderList = menuOrder.getMenuOptionMenuOrderList();
            ArrayList<OrderGetDetailResponseDto.OptionDto> optionDtos = new ArrayList<>();
            for (MenuOptionMenuOrder menuOptionMenuOrder : menuOptionMenuOrderList) {
                optionDtos.add(
                        OrderGetDetailResponseDto.OptionDto.builder()
                                .optionName(menuOptionMenuOrder.getMenuOptionName())
                                .optionCount(menuOptionMenuOrder.getMenuOptionCount())
                                .build()
                );
            }

            menuDtos.add(
                menuDtoBuilder
                        .orderList(optionDtos)
                        .build()
            );
        }
        return orderDtoBuilder.menuList(menuDtos).build();
    }

    public static OrderGetDataDto entityToGetDataDto(Order order) {
        return OrderGetDataDto.builder()
                .orderId(order.getId())
                .orderStatus(order.getOrderStatus().name())
                .customerName(order.getCustomerName())
                .storeId(order.getStoreId())
                .storeName(order.getStoreName())
                .createdAt(CommonUtil.LDTToString(order.getCreatedAt()))
                .updatedAt(CommonUtil.LDTToString(order.getUpdatedAt()))
                .build();
    }

    public static OrderStatusUpdateResponseDto entityToStatusUpdateResponseDto(Order order, Order.OrderStatus beforeStatus) {
        return OrderStatusUpdateResponseDto.builder()
                .beforeStatus(beforeStatus.name())
                .afterStatus(order.getOrderStatus().name())
                .build();
    }
}
