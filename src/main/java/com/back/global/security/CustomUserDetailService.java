package com.back.global.security;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberService.findByUsername(username).get();

        return new SecurityUser(
                member.getId(),
                member.getUsername(),
                member.getPassword(),
                member.getNickname(),
                List.of()//권한 설정
        );
    }
}
