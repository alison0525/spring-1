package com.back.global.initData;

import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.entity.Post;
import com.back.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class BaseInitData {

    @Autowired
    @Lazy
    private BaseInitData self;
    private final PostService postService;
    private final MemberService memberService;

    @Bean
    ApplicationRunner initDataRunner() {
        return args -> {

            self.work1();
            self.work2();

        };

    }

    @Transactional
    public void work1() {
        if(memberService.count() > 0) {
            return;
        }

        memberService.join("system", "system", "시스템","system");
        memberService.join("admin", "admin", "운영자", "admin");
        memberService.join("user1", "1234", "유저1", "user1");
        memberService.join("user2", "1234", "유저2", "user2");
        memberService.join("user3", "1234", "유저3", "user3");

    }

    @Transactional
    public void work2() {
        if(postService.count() > 0) {
            return;
        }

        Member member1 = memberService.findByUsername("user1").get();
        Member member2 = memberService.findByUsername("user2").get();
        Member member3 = memberService.findByUsername("user3").get();

        Post post1 = postService.write(member1, "제목1", "내용1");
        Post post2 = postService.write(member1, "제목2", "내용2");
        Post post3 = postService.write(member2, "제목3", "내용3");

        post1.addComment(member1, "댓글 1-1");
        post1.addComment(member1, "댓글 1-2");
        post1.addComment(member1, "댓글 1-3");
        post2.addComment(member2, "댓글 2-1");
        post2.addComment(member2, "댓글 2-2");
    }
}
