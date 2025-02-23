package com.fortest.orderdelivery.app;

import com.fortest.orderdelivery.app.domain.ai.entity.AiRequest;
import com.fortest.orderdelivery.app.domain.ai.repository.AiRequestRepository;
import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.fortest.orderdelivery.app.domain.area.repository.AreaRepository;
import com.fortest.orderdelivery.app.domain.category.entity.Category;
import com.fortest.orderdelivery.app.domain.category.entity.CategoryStore;
import com.fortest.orderdelivery.app.domain.category.repository.CategoryRepository;
import com.fortest.orderdelivery.app.domain.delivery.entity.Delivery;
import com.fortest.orderdelivery.app.domain.delivery.repository.DeliveryRepository;
import com.fortest.orderdelivery.app.domain.image.repository.ImageRepository;
import com.fortest.orderdelivery.app.domain.menu.entity.ExposeStatus;
import com.fortest.orderdelivery.app.domain.menu.entity.Menu;
import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuOptionRepository;
import com.fortest.orderdelivery.app.domain.menu.repository.MenuRepository;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOptionMenuOrder;
import com.fortest.orderdelivery.app.domain.order.entity.MenuOrder;
import com.fortest.orderdelivery.app.domain.order.entity.Order;
import com.fortest.orderdelivery.app.domain.order.repository.OrderRepository;
import com.fortest.orderdelivery.app.domain.payment.entity.Payment;
import com.fortest.orderdelivery.app.domain.payment.entity.PaymentAgent;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentAgentRepository;
import com.fortest.orderdelivery.app.domain.payment.repository.PaymentRepository;
import com.fortest.orderdelivery.app.domain.review.entity.Review;
import com.fortest.orderdelivery.app.domain.review.repository.ReviewRepository;
import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.fortest.orderdelivery.app.domain.store.repository.StoreRepository;
import com.fortest.orderdelivery.app.domain.user.entity.RoleType;
import com.fortest.orderdelivery.app.domain.user.entity.User;
import com.fortest.orderdelivery.app.domain.user.repository.RoleTypeRepository;
import com.fortest.orderdelivery.app.domain.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@ActiveProfiles({"test"})
@SpringBootTest
public class TestDataMaker {

    @Autowired
    AiRequestRepository aiRequestRepository;
    @Autowired
    AreaRepository areaRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    MenuRepository menuRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    StoreRepository storeRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    MenuOptionRepository menuOptionRepository;
    @Autowired
    PaymentAgentRepository paymentAgentRepository;
    @Autowired
    RoleTypeRepository roleTypeRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("테스트 데이터 생성")
    @Rollback(value = false)
    @Transactional
    void mkTestData(){

        // 1. 권한 생성
        String[] roleNames = {"CUSTOMER","OWNER","MANAGER","MASTER"};
        ArrayList<RoleType> roleTypes = new ArrayList<>();
        for (String roleName : roleNames) {
            RoleType roleType = new RoleType(null, RoleType.RoleName.valueOf(roleName), roleName + "권한 입니다.");
            roleTypes.add(roleType);
        }
        roleTypeRepository.saveAll(roleTypes);
        roleTypeRepository.flush();

        // 2. 유저 생성
        HashMap<String, ArrayList<User>> roleNameUserMap = new HashMap<>();
        for (String roleName : roleNames) {
            roleNameUserMap.put(roleName, new ArrayList<>());
        }
        ArrayList<User> users = new ArrayList<>();
        int roleIndex = 0;
        for (int i = 0; i <6; i++) {
            if (i == 2) {
                roleIndex = 1;
            }
            if (i == 4) {
                roleIndex = 2;
            }
            if (i == 5) {
                roleIndex = 3;
            }
            RoleType roleType = roleTypes.get(roleIndex);
            User user = User.builder()
                    .username("customer" + i)
                    .password(passwordEncoder.encode("pswd_customer" + i))
                    .nickname("nick_customer" + i)
                    .email("customer" + i + "@naver.com")
                    .roleType(roleType)
                    .isPublic(true)
                    .build();
            users.add(user);
            user.isCreatedBy(user.getId());
            roleNameUserMap.get(roleType.getRoleName().name()).add(user);
        }
        userRepository.saveAll(users);
        userRepository.flush();

        Long managerId = roleNameUserMap.get("MANAGER").get(0).getId();

        // 3. 카테고리 생성
        String[] categoryNames = {"한식","중식","일식","양식"};
        ArrayList<Category> categories = new ArrayList<>();
        for (String categoryName : categoryNames) {
            Category category = Category.builder()
                    .categoryStoreList(new ArrayList<>())
                    .name(categoryName)
                    .build();
            category.isCreatedBy(managerId);
            categories.add(category);
        }
        categoryRepository.saveAll(categories);
        categoryRepository.flush();

        // 4. 지역 생성
        ArrayList<Area> areas = new ArrayList<>();
        // 1 : 0 ~ 8
        // 2 : 9 ~ 17
        // 3 : 18 ~ 26
        String[] citys = {"도시1", "도시2", "도시3"};
        String[] districts = {"구1", "구2", "구3"};
        String[] streets = {"로1", "로2", "로3"};
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    Area area = Area.builder()
                            .city(citys[i])
                            .district(districts[j])
                            .street(streets[k])
                            .build();
                    area.isCreatedBy(managerId);
                    areas.add(area);
                }
            }
        }
        areaRepository.saveAll(areas);
        areaRepository.flush();

        // 5. 결제 에이전트 생성
        String[] paymentNames = {"KAKAO", "NAVER", "TOSS"};
        ArrayList<PaymentAgent> paymentAgents = new ArrayList<>();
        for (String paymentName : paymentNames) {
            PaymentAgent paymentAgent = PaymentAgent.builder()
                    .name(paymentName)
                    .build();
            paymentAgents.add(paymentAgent);
        };
        paymentAgentRepository.saveAll(paymentAgents);
        paymentAgentRepository.flush();

        // 6. 가게 생성
        String[] storeNames = {"버거킹", "롯데리아", "집게리아", "파이브가이즈"};
        ArrayList<Store> stores = new ArrayList<>();
        int areaIndex = 0;
        int ownerIndex = 0;
        ArrayList<User> owners = roleNameUserMap.get("OWNER");
        Map<Store, User> storeOwnerMap = new HashMap<>();
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0 -> {
                    ownerIndex = 0;
                    areaIndex = 0;
                }
                case 1 -> {
                    ownerIndex = 0;
                    areaIndex = 1;
                }
                case 2 -> {
                    ownerIndex = 1;
                }
                case 3 -> {
                    ownerIndex = 1;
                    areaIndex = 18;
                }
            }

            User owner = owners.get(ownerIndex);
            String storeName = storeNames[i];
            Store store = Store.builder()
                    .name(storeName)
                    .area(areas.get(areaIndex))
                    .detailAddress("건물" + i + i + i + "호")
                    .ownerName(owner.getUsername())
                    .categoryStoreList(new ArrayList<>())
                    .build();
            storeOwnerMap.put(store, owner);
            store.isCreatedBy(owner.getId());
            stores.add(store);
        }
        storeRepository.saveAll(stores);
        storeRepository.flush();

        // 7. 카테고리-가게 생성
        // 가게 총 4개 , 카테고리 총 4개 -> 0,1 가게는 카테고리를 2개씩 세팅
        for (int i = 0; i < 4; i++) {
            CategoryStore categoryStore1 = CategoryStore.builder().build();
            categoryStore1.bindCategory(categories.get(i));
            categoryStore1.bindStore(stores.get(i));
            if (i < 2) {
                CategoryStore categoryStore2 = CategoryStore.builder().build();
                categoryStore2.bindCategory(categories.get(i + 2));
                categoryStore2.bindStore(stores.get(i));
            }
        }
        storeRepository.saveAll(stores);
        storeRepository.flush();

        // 8. AI 요청 기록 생성
        ArrayList<AiRequest> aiRequests = new ArrayList<>();
        for (Store store : stores) {
            for (int i = 0; i < 5; i++) {
                AiRequest aiRequest = AiRequest.builder()
                        .storeId(store.getId())
                        .question(store.getName() + " 메뉴 질문_" + i)
                        .answer(store.getName() + " 답변_" + i)
                        .build();
                aiRequests.add(aiRequest);
            }
        }
        aiRequestRepository.saveAll(aiRequests);
        aiRequestRepository.flush();

        // 메뉴 및 옵션 생성
        HashMap<Store, HashMap<Menu, List<MenuOption>>> storeMenuMenuOptionMap = new HashMap<>();
        for (Store store : stores) {
            HashMap<Menu, List<MenuOption>> menuMenuOptionMap = new HashMap<>();
            for (int i = 1; i < 6; i++) {
                // 9. 메뉴 생성 : 가게당 5개 생성
                Menu menu = Menu.builder()
                        .name(store.getName() + " 메뉴" + i)
                        .description(store.getName() + " " + i + " 가지 맛")
                        .storeId(store.getId())
                        .price(i * 1000)
                        .exposeStatus(ExposeStatus.ONSALE)
                        .build();
                User owner = storeOwnerMap.get(store);
                menu.isCreatedBy(owner.getId());
                ArrayList<MenuOption> menuOptions = new ArrayList<>();
                // 10. 옵션 생성
                for (int j = 1; j < 6; j++) {
                    MenuOption menuOption = MenuOption.builder()
                            .name(store.getName() + "." + "옵션 " + j)
                            .description(j + " 번째 맛")
                            .price(j * 100)
                            .menu(menu)
                            .exposeStatus(ExposeStatus.ONSALE)
                            .build();
                    menuOption.isCreatedBy(owner.getId());
                    menuOptions.add(menuOption);
                }
                menuMenuOptionMap.put(menu, menuOptions);
            }
            storeMenuMenuOptionMap.put(store, menuMenuOptionMap);
        }
        ArrayList<Menu> menus = new ArrayList<>();
        ArrayList<MenuOption> menuOptions = new ArrayList<>();
        for (Store store : storeMenuMenuOptionMap.keySet()) {
            HashMap<Menu, List<MenuOption>> menuListHashMap = storeMenuMenuOptionMap.get(store);
            Set<Menu> menuSet = menuListHashMap.keySet();
            menus.addAll(menuSet);
            for (Menu menu : menuSet) {
                menuOptions.addAll(menuListHashMap.get(menu));
            }
        }
        menuRepository.saveAll(menus);
        menuRepository.flush();
        menuOptionRepository.saveAll(menuOptions);
        menuOptionRepository.flush();

        // 11. 주문 생성
        ArrayList<Order> orders = new ArrayList<>();
        ArrayList<Payment> payments = new ArrayList<>();
        ArrayList<Delivery> deliveries = new ArrayList<>();
        ArrayList<Review> reviews = new ArrayList<>();
        for (User customer : roleNameUserMap.get("CUSTOMER")) {
            int tempNum = 1;

            for (Store store : stores) {
                Order order = Order.builder()
                        .storeId(store.getId())
                        .storeName(store.getName())
                        .customerName(customer.getUsername())
                        .orderStatus(Order.OrderStatus.WAIT)
                        .orderType(tempNum % 2 == 0 ? Order.OrderType.INSTORE : Order.OrderType.DELIVERY)
                        .menuOrderList(new ArrayList<>())
                        .build();
                orders.add(order);
                order.isCreatedBy(customer.getId());
                orderRepository.save(order);
                orderRepository.flush();

                // 12. 배달 생성
                if (order.getOrderType() == Order.OrderType.DELIVERY) {
                    Delivery delivery = Delivery.builder()
                            .customerName(customer.getUsername())
                            .orderId(order.getId())
                            .address("주소:" + store.getName() + order.getId() + "시 고백구 행복동")
                            .status(Delivery.Status.END)
                            .build();
                    delivery.isCreatedBy(customer.getId());
                    deliveries.add(delivery);
                }

                // 13. 리뷰 생성
                Review review = Review.builder()
                        .writerId(customer.getId())
                        .orderId(order.getId())
                        .rate((System.currentTimeMillis() % 10) - 4)
                        .contents("리뷰: " + store.getName() + "마시쒀요")
                        .storeId(store.getId())
                        .storeName(store.getName())
                        .build();
                review.isCreatedBy(customer.getId());

                int totalPrice = 0;
                HashMap<Menu, List<MenuOption>> menuListHashMap = storeMenuMenuOptionMap.get(store);
                List<Menu> menuList = menuListHashMap.keySet().stream().collect(Collectors.toList());

                // 14. 주문-메뉴 생성
                for (int i = 1; i < 3; i++) {
                    Menu menu = menuList.get(i);
                    List<MenuOption> menuOptionsList = menuListHashMap.get(menu);
                    MenuOrder menuOrder = MenuOrder.builder()
                            .menuId(menu.getId())
                            .menuName(menu.getName())
                            .count(i)
                            .price(menu.getPrice())
                            .menuOptionMenuOrderList(new ArrayList<>())
                            .build();
                    totalPrice += menuOrder.getPrice() * menuOrder.getCount();
                    order.addMenuOrder(menuOrder);

                    // 15. 주문-메뉴-옵션 생성
                    for (int j = 1; j < 4; j++) {
                        MenuOption menuOption = menuOptionsList.get(j);
                        MenuOptionMenuOrder menuOptionMenuOrder = MenuOptionMenuOrder.builder()
                                .menuOptionId(menuOption.getId())
                                .menuOptionName(menuOption.getName())
                                .menuOptionCount(j)
                                .menuOptionPrice(menuOption.getPrice())
                                .build();
                        totalPrice += menuOptionMenuOrder.getMenuOptionPrice() * menuOptionMenuOrder.getMenuOptionCount();
                        menuOrder.addMenuOptionMenuOrder(menuOptionMenuOrder);
                    }
                }

                order.updateTotalPrice(totalPrice);

                if ( tempNum >= paymentAgents.size()) {
                    tempNum = 0;
                }

                // 16. 결제 생성
                Payment payment = Payment.builder()
                        .orderId(order.getId())
                        .customerName(customer.getUsername())
                        .paymentAgent(paymentAgents.get(tempNum))
                        .paymentPid("PID:" + store.getName() + System.currentTimeMillis() + Math.random())
                        .status(Payment.Status.COMPLETE)
                        .price(totalPrice)
                        .build();
                payments.add(payment);
                tempNum ++;
            }
        }

        orderRepository.saveAll(orders);
        orderRepository.flush();
        paymentRepository.saveAll(payments);
        paymentRepository.flush();
        deliveryRepository.saveAll(deliveries);
        deliveryRepository.flush();
        reviewRepository.saveAll(reviews);
        reviewRepository.flush();

        // 이미지 테이블은 S3 생성 로직 테스트에 불리하므로 생성 X
    }
}
