package com.fortest.orderdelivery.app.domain.store.repository;

import com.fortest.orderdelivery.app.domain.store.entity.Store;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.fortest.orderdelivery.app.domain.store.entity.QStore.store;


@RequiredArgsConstructor
@Repository
public class StoreQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public Optional<Store> findStoreDetail (String storeId) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(store)
                        .where(
                                store.deletedAt.isNull(),
                                store.id.eq(storeId)
                        )
                        .fetchOne()
        );
    }
}
