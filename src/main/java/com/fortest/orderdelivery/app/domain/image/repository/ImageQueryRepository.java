package com.fortest.orderdelivery.app.domain.image.repository;

import static com.fortest.orderdelivery.app.domain.image.entity.QImage.image;

import com.fortest.orderdelivery.app.domain.image.entity.Image;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Integer getMaxMenuImageSequence(String menuId) {
        Integer result = queryFactory
            .select(image.sequence.max())
            .from(image)
            .where(
                image.menu.id.eq(menuId),
                image.deletedAt.isNull()
            )
            .fetchOne();

        return result == null ? 0 : result;
    }

    public Integer getMaxMenuOptionImageSequence(String menuOptionId) {
        Integer result = queryFactory
            .select(image.sequence.max())
            .from(image)
            .where(
                image.menuOption.id.eq(menuOptionId),
                image.deletedAt.isNull()
            )
            .fetchOne();

        return result == null ? 0 : result;
    }

    public List<Image> getImageListByMenuOptionId(String menuOptionId) {
        return queryFactory
            .selectFrom(image)
            .where(
                image.menuOption.id.eq(menuOptionId),
                image.deletedAt.isNull()
            )
            .fetch();
    }

    public List<Image> getImageListByMenuId(String menuId) {
        return queryFactory
            .selectFrom(image)
            .where(
                image.menu.id.eq(menuId),
                image.deletedAt.isNull()
            )
            .fetch();
    }

    public List<Image> deleteImagesAndReturn(List<String> imageIdList, Long userId) {
        List<Image> imagesToDelete = queryFactory
            .selectFrom(image)
            .where(image.id.in(imageIdList))
            .fetch();

        queryFactory
            .update(image)
            .set(image.deletedAt, LocalDateTime.now())
            .set(image.deletedBy, userId)
            .where(image.id.in(imageIdList))
            .execute();

        return imagesToDelete;
    }
}
