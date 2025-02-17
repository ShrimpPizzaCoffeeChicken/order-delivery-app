package com.fortest.orderdelivery.app.domain.store.controller;

import com.fortest.orderdelivery.app.domain.store.dto.StoreRequestDto;
import com.fortest.orderdelivery.app.domain.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping("/stores")
    public void saveStore(@RequestBody StoreRequestDto storeRequestDto) {
        // TODO : TEMP : userId 를 UserDetail 에서 획득해야함
        storeService.saveStores(storeRequestDto, 123L);
    }
}
