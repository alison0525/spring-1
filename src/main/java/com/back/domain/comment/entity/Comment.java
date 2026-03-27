package com.back.domain.comment.entity;

import com.back.domain.member.entity.Member;
import com.back.domain.post.entity.Post;
import com.back.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Comment extends BaseEntity {
    private String content;
    @ManyToOne
    private Member author;
    @ManyToOne
    private Post post;

    public Comment(Member author,String content, Post post) {
        this.content = content;
        this.author = author;
        this.post = post;
    }

    public void update(String content){
        this.content = content;
    }
}
