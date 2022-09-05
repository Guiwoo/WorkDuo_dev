package com.workduo.error.global.exception;

import lombok.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomMethodArgumentNotValidException extends RuntimeException {

    private BindingResult bindingResult;
}
