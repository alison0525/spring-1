package com.back.global.rq;


import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request; // 프록시 객체
    private final HttpServletResponse response;
    private final MemberService memberService;

    public Member getActor() {

        String authorizationHeader = getHeader("Authorization", "");
        String apiKey ;
        String accessToken;

        //헤더 방식
        if(!authorizationHeader.isBlank()){
            if(!authorizationHeader.startsWith("Bearer ")){
                throw new ServiceException("401-2","잘못된 형식의 인증 데이터입니다.");
            }
            String[] headerAuthorizationBits = authorizationHeader.split(" ", 3);
            apiKey = headerAuthorizationBits[1];
            accessToken = headerAuthorizationBits.length == 3 ? headerAuthorizationBits[2] : "";

        }else{
            //쿠키 방식
            apiKey = getCookieValue("apiKey", "");
            accessToken = getCookieValue("accessToken", "");
        }

        Member member = null;

        if (apiKey.isBlank())
            throw new ServiceException("401-1", "apiKey가 존재하지 않습니다.");

        if (!accessToken.isBlank()) {
            Map<String, Object> payload = memberService.payloadOrNull(accessToken);

            if (payload != null) {
                int id = (int) payload.get("id");
                String username = (String) payload.get("username");
                member = new Member(id, username);
            }
        }
        //accessToken으로 인증이 제대로 이루어지지 않은 경우
        if (member == null) {
            member = memberService
                    .findByApiKey(apiKey)
                    .orElseThrow(() -> new ServiceException("401-4", "API 키가 유효하지 않습니다."));
        }


        return member;
    }

    public void addCookie(String name, String value){

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");//모든 경로에서 접근 가능
        cookie.setHttpOnly(true);//xss 방지
        cookie.setDomain("localhost");//로컬호스트 도메인에서만 가능

        response.addCookie(
                cookie
        );
    }

    private String getHeader(String name, String defaultValue) {
        return Optional
                .ofNullable(request.getHeader(name))
                .filter(headerValue -> !headerValue.isBlank())
                .orElse(defaultValue);
    }

    private String getCookieValue(String name, String defaultValue) {
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
