package com.back.domain.comment.controller;

import com.back.domain.comment.dto.CommentDto;
import com.back.domain.comment.entity.Comment;
import com.back.domain.member.entity.Member;
import com.back.domain.member.service.MemberService;
import com.back.domain.post.entity.Post;
import com.back.domain.post.service.PostService;
import com.back.global.rq.Rq;
import com.back.global.rsData.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts/{postId}/comments")
@Tag(name = "ApiV1CommentController", description="댓글 API")
public class ApiV1CommentController {

    private final PostService postService;
    private final MemberService memberService;
    private final Rq rq;

    @GetMapping
    @Operation(summary = "댓글 다건 조회")
    public List<CommentDto> list(@PathVariable("postId") int postId){
        Post post = postService.findById(postId).get();
        List<Comment> comments = post.getComments().reversed();

        List<CommentDto> commentDtoList = comments.stream()
                .map(CommentDto::new)
                .toList();
        return commentDtoList;
    }

    @Operation(summary = "댓글 단건 조회")
    @GetMapping("/{commentId}")
    public CommentDto detail(@PathVariable("postId") int postId, @PathVariable("commentId") int commentId){
        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();
        return new CommentDto(comment);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{commentId}")
    @Transactional
    public RsData<Void> delete(
            @PathVariable int postId,
            @PathVariable int commentId
    ){
        Member actor = rq.getActor();
        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();

        comment.checkActorDelete(actor);

        post.deleteComment(commentId);

        return new RsData<>(
                "%d번 댓글이 삭제되었습니다.".formatted(commentId),
                "200-1"
        );
    }

    record CommentWriteBody(

            @NotBlank(message = "02-content-내용은 필수입니다.")
            @Size(min = 2, max = 100, message = "04-content-내용은 2자 이상 100자 이하로 입력해주세요.")
            String content
    ){}

    record CommentWriteResBody(
            CommentDto commentDto
    ){}

    @Operation(summary = "댓글 작성")
    @PostMapping
    @Transactional
    public RsData<CommentWriteResBody> write(
            @RequestBody @Valid CommentWriteBody reqBody,
            @PathVariable int postId
    ){
        Post post = postService.findById(postId).get();
        Member actor  = rq.getActor();
        Comment comment = post.addComment(actor,reqBody.content);

        postService.flush();

        return new RsData<>(
                "%d번 댓글이 작성되었습니다.".formatted(comment.getId()),
                "201-1",
                new CommentWriteResBody(
                        new CommentDto(comment)
                )
        );

    }

    record CommentModifyReqBody(
            @NotBlank(message = "02-content-내용은 필수입니다.")
            @Size(min = 2, max = 100, message = "04-content-내용은 2자 이상 100자 이하로 입력해주세요.")
            String content
    ){}

    @Operation(summary = "댓글 수정")
    @PutMapping("/{commentId}")
    @Transactional
    public RsData<Void> modify(@RequestBody @Valid CommentModifyReqBody reqBody, @PathVariable int postId, @PathVariable int commentId){
        Member actor = rq.getActor();
        Post post = postService.findById(postId).get();
        Comment comment = post.findCommentById(commentId).get();
        comment.checkActorModify(actor);
        post.modifyComment(commentId, reqBody.content);

        return new RsData<>(
               "%d번 댓글이 수정되었습니다.".formatted(postId),
               "200-1"
        );
    }
}
