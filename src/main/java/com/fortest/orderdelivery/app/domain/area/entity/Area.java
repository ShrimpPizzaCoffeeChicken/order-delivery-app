package com.fortest.orderdelivery.app.domain.area.entity;

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
@Table(name = "p_area")
@Entity
public class Area extends BaseDataEntity {

    @Id
    @Column(length = 50)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(length = 50)
    private String city;

    @Column(length = 50)
    private String district;

    @Column(length = 50)
    private String street;

    public String getPlainAreaName() {
        String space = "";
        StringBuilder sb = new StringBuilder();
        return sb
                .append(this.city)
                .append(space)
                .append(this.district)
                .append(space)
                .append(this.street)
                .toString();
    }
}
