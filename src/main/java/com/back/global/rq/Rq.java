package com.back.global.rq;


import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.security.SecurityUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request; // 프록시 객체
    private final HttpServletResponse response;
    private final MemberService memberService;

    public Member getActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SecurityUser securityUser =  (SecurityUser) authentication.getPrincipal();
        return new Member(securityUser.getId(), securityUser.getUsername(), securityUser.getNickname());
    }


    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public void addCookie(String name, String value){

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");//모든 경로에서 접근 가능
        cookie.setHttpOnly(true);//xss 방지
        cookie.setDomain("localhost");//로컬호스트 도메인에서만 가능
        cookie.setSecure(true); // http x https o
        cookie.setAttribute("SameSite", "Strict");

        response.addCookie(
                cookie
        );
    }

    public String getHeader(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getHeader(name))
                .filter(headerValue -> !headerValue.isBlank())
                .orElse(defaultValue);
    }

    public String getCookieValue(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getCookies())
                .flatMap(
                        cookies ->
                                Arrays.stream(cookies)
                                        .filter(cookie -> cookie.getName().equals(name))
                                        .map(Cookie::getValue)
                                        .filter(value -> !value.isBlank())
                                        .findFirst()
                )
                .orElse(defaultValue);
    }

    public void deleteCookie(String name) {
        Cookie cookie = new Cookie(name,"");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("localhost");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
