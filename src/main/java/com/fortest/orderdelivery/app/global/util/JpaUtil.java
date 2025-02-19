package com.fortest.orderdelivery.app.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class JpaUtil {

    /**
     * 페이징 객체를 반환
     * @param page : 요청 페이지
     * @param size : 한페이지에 노출될 엘리먼트 수
     * @param orderby : 정렬 기준 엔티티의 필드 이름
     * @param sort : 정렬 순서(DESC, ASC)
     * @return
     */
    public static PageRequest getNormalPageable(Integer page, Integer size, String orderby, String sort) {
        Sort sortAndOrderBy = Sort.by(orderby);
        sortAndOrderBy = "DESC".equals(sort) ? sortAndOrderBy.descending() : sortAndOrderBy.ascending();

        return PageRequest.of(page - 1 , size, sortAndOrderBy);
    }
}
