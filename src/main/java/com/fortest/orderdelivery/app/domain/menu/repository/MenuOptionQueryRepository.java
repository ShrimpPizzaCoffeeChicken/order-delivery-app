package com.fortest.orderdelivery.app.domain.menu.repository;

import static com.fortest.orderdelivery.app.domain.menu.entity.QMenu.menu;
import static com.fortest.orderdelivery.app.domain.menu.entity.QMenuOption.menuOption;

import com.fortest.orderdelivery.app.domain.menu.entity.MenuOption;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MenuOptionQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<MenuOption> getMenuOptionsAndMenusByMenuOptionIds(List<String> menuOptionIdList) {
        return queryFactory
            .selectFrom(menuOption)
            .leftJoin(menuOption.menu, menu)
            .fetchJoin()
            .where(menuOption.id.in(menuOptionIdList))
            .fetch();
    }

}
