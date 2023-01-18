package com.core.error.global.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindingResult;

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
                       .map(DefaultMessageSourceResolvable::getDefaultMessage)
                       .collect(Collectors.toList()))
               .build();
   }
}
