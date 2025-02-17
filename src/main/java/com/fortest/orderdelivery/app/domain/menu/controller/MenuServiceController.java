package com.fortest.orderdelivery.app.domain.menu.controller;

import com.fortest.orderdelivery.app.domain.menu.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service/menus")
@RequiredArgsConstructor
public class MenuServiceController {
    private final MenuService menuService;

    @PostMapping
    public ResponseEntity<?> saveMenu() {
        return null;
    }
}
