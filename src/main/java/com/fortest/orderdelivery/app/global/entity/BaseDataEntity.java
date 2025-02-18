package com.fortest.orderdelivery.app.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDataEntity {

    @Column(name = "created_by", updatable = false)
    private Long createdBy;
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;
    private LocalDateTime updatedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;
    private LocalDateTime deletedAt;

    public void isCreatedBy(Long userId) {
        this.createdBy = userId;
    }

    public void isUpdatedNow(Long userId) {
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = userId;
    }

    public void isDeletedNow(Long userId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
    }
}
