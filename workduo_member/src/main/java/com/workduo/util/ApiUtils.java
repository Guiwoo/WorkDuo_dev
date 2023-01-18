package com.workduo.util;

import lombok.Getter;
import lombok.Setter;

public class ApiUtils {

    public static <T> ApiResult<T> success(T response) {
        return new ApiResult<>("T", response);
    }

    @Getter
    @Setter
    public static class ApiResult<T> {
        private final String success;
        private final T result;

        private ApiResult(String success, T response) {
            this.success = success;
            this.result = response;
        }
    }
}
