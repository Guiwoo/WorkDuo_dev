package com.workduo.error.global;

import com.workduo.error.global.type.GlobalExceptionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.rmi.ServerException;

@RestController
@RequestMapping("/exception")
public class GlobalExceptionController {
    @GetMapping()
    public void globalException() throws ServerException {
        throw new ServerException(GlobalExceptionType.INTERNAL_ERROR.getMessage());
    }

}
