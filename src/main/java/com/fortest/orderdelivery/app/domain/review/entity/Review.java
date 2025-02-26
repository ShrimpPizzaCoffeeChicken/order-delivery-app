package com.fortest.orderdelivery.app.domain.review.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_review")
@Entity
public class Review extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Long writerId;

    @Column(length = 50)
    private String orderId;

    @Min(1)
    @Max(5)
    private Integer rate;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Column(length = 50)
    private String storeId;

    @Column(length = 100)
    private String storeName;
}
