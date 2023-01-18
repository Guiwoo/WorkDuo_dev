package com.workduo.member.history.service;

import javax.servlet.http.HttpServletRequest;

public interface LoginHistoryService {
    void saveLoginHistory(String email, HttpServletRequest req) throws Exception;
}
