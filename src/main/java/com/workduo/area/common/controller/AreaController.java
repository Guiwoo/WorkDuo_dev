package com.workduo.area.common.controller;

import com.workduo.area.common.service.AreaService;
import com.workduo.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/area")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @PostMapping("")
    public ResponseEntity<?> insertArea() throws Exception {
        areaService.insertArea();

        return new ResponseEntity<>(
                CommonResponse.from(),
                HttpStatus.CREATED
        );
    }
}
