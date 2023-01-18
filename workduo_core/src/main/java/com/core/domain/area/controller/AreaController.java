package com.core.domain.area.controller;

import com.core.domain.area.service.AreaService;
import com.core.domain.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping("")
    @Operation(hidden = true)
    public ResponseEntity<?> insertArea() throws Exception {
        areaService.insertArea();

        return new ResponseEntity<>(
                CommonResponse.ok(),
                HttpStatus.CREATED
        );
    }
}

