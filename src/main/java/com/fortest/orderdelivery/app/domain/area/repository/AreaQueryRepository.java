package com.fortest.orderdelivery.app.domain.area.repository;

import com.fortest.orderdelivery.app.domain.area.entity.Area;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.fortest.orderdelivery.app.domain.area.entity.QArea.*;

@RequiredArgsConstructor
@Repository
public class AreaQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Area> findByComponents(String city, String district, String street) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(area)
                        .where(
                                area.city.eq(city),
                                area.district.eq(district),
                                area.street.eq(street)
                        )
                        .fetchOne()
        );
    }
}
