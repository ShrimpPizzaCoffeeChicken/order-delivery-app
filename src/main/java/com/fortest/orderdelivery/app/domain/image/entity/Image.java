package com.fortest.orderdelivery.app.domain.image.entity;

import com.fortest.orderdelivery.app.global.entity.BaseDataEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "p_image")
@Entity
public class Image extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private Integer sequence;

    @Column(length = 50)
    private String menuId;

    @Column(length = 50)
    private String optionId;

    @Column(length = 200, nullable = false)
    private String s3Url;

}
