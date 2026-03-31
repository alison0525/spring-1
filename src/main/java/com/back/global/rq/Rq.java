package com.back.global.rq;


import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.global.exception.ServiceException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Rq {
    private final HttpServletRequest request; // 프록시 객체
    private final HttpServletResponse response;
    private final MemberService memberService;

    public void addCookie(String name, String value){

        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");//모든 경로에서 접근 가능
        cookie.setHttpOnly(true);//xss 방지
        cookie.setDomain("localhost");//로컬호스트 도메인에서만 가능

        response.addCookie(
                cookie
        );
    }

    public Member getActor() {

        String authorizationHeader = request.getHeader("Authorization");

        if(authorizationHeader == null){
            throw new ServiceException("401-1", "인증 정보가 헤더에 존재하지 않습니다.");
        }

        if(!authorizationHeader.startsWith("Bearer ")){
            throw new ServiceException("401-2","잘못된 형식의 인증 데이터입니다.");
        }

        String apiKey = authorizationHeader.replace("Bearer ","");


        return memberService.findByApiKey(apiKey).orElseThrow(
                ()-> new ServiceException("401-1","유효하지 않은 API 키 입니다.")
        );
    }
}
