package com.fortest.orderdelivery.app.domain.image.repository;

import com.fortest.orderdelivery.app.domain.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image,String> {

}
