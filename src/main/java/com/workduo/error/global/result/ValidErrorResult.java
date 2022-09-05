package com.workduo.error.global.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidErrorResult {

    private String success;
    private List<String> messages;

   public static ValidErrorResult of(BindingResult bindingResult) {
       return ValidErrorResult.builder()
               .success("F")
               .messages(bindingResult.getAllErrors().stream()
                       .map(error -> error.getDefaultMessage())
                       .collect(Collectors.toList()))
               .build();
   }
}
