package com.back.domain.comment.entity;

import com.back.domain.member.entity.Member;
import com.back.domain.post.entity.Post;
import com.back.global.entity.BaseEntity;
import com.back.global.exception.ServiceException;
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

    public void checkActorModify(Member actor) {
        if(!this.author.getId().equals(actor.getId())) {
            throw new ServiceException("403-1", "댓글 수정 권한이 없습니다.");
        }
    }

    public void checkActorDelete(Member actor) {
        if(!this.author.getId().equals(actor.getId())) {
            throw new ServiceException("403-2", "댓글 삭제 권한이 없습니다.");
        }
    }
}
