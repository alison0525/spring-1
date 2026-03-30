package com.back.domain.post.post.controller;

import com.back.domain.post.controller.ApiV1PostController;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsInRelativeOrder;
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ApiV1PostControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("글 다건 조회")
    void t1() throws Exception {
        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts")
                )
                .andDo(print());

        List<Post> posts = postRepository.findAll();

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("list"))
                .andExpect(status().isOk());

        resultActions
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[*].id", containsInRelativeOrder(3, 1)))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].createDate").exists())
                .andExpect(jsonPath("$[0].modifyDate").exists())
                .andExpect(jsonPath("$[0].title").value("제목3"))
                .andExpect(jsonPath("$[0].content").value("내용3"))
                .andExpect(jsonPath("$[0].authorId").value(4))
                .andExpect(jsonPath("$[0].authorName").value("유저2"));


        /*for(int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);

            resultActions
                    .andExpect(jsonPath("$[%d].id".formatted(i)).value(post.getId()))
                    .andExpect(jsonPath("$[%d].createDate".formatted(i)).value(matchesPattern(post.getCreateDate().toString().replaceAll("0+$", "") + ".*")))
                    .andExpect(jsonPath("$[%d].modifyDate".formatted(i)).value(matchesPattern(post.getModifyDate().toString().replaceAll("0+$", "") + ".*")))
                    .andExpect(jsonPath("$[%d].title".formatted(i)).value(post.getTitle()))
                    .andExpect(jsonPath("$[%d].content".formatted(i)).value(post.getContent()));
        }*/
    }

    @Test
    @DisplayName("글 단건 조회")
    void t2() throws Exception {

        int targetId = 1;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/%d".formatted(targetId))
                )
                .andDo(print());

        Post post = postRepository.findById(targetId).get();
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.createDate").value(matchesPattern(post.getCreateDate().toString().replaceAll("0+$", "") + ".*")))
                .andExpect(jsonPath("$.modifyDate").value(matchesPattern(post.getModifyDate().toString().replaceAll("0+$", "") + ".*")))
                .andExpect(jsonPath("$.title").value(post.getTitle()))
                .andExpect(jsonPath("$.content").value(post.getContent()))
                .andExpect(jsonPath("$.authorId").value(3))
                .andExpect(jsonPath("$.authorName").value("유저1"));

    }

    @Test
    @DisplayName("글 단건 조회, 존재하지 않는 글")
    void t3() throws Exception {
        int targetId = Integer.MAX_VALUE;

        ResultActions resultActions = mvc
                .perform(
                        get("/api/v1/posts/%d".formatted(targetId))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("detail"))
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("글 생성")
    void t4() throws Exception {
        String title = "제목입니다";
        String content = "내용입니다";
        String apiKey = "user1";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .header("Authorization", apiKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.msg").value("%d번 글이 작성되었습니다.".formatted(4)))
                .andExpect(jsonPath("$.resultCode").value("201-1"))
                .andExpect(jsonPath("$.data.postDto.id").value(4))
                .andExpect(jsonPath("$.data.postDto.createDate").exists())
                .andExpect(jsonPath("$.data.postDto.modifyDate").exists())
                .andExpect(jsonPath("$.data.postDto.title").value(title))
                .andExpect(jsonPath("$.data.postDto.content").value(content));
    }

    @Test
    @DisplayName("글 작성, 제목이 입력되지 않은 경우")
    void t5() throws Exception {

        String title="";
        String content="내용입니다";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("title-NotBlank-01-title-제목은 필수입니다.\ntitle-Size-03-title-제목은 2자 이상 10자 이하로 입력해주세요."));
    }

    @Test
    @DisplayName("글 작성, 내용이 입력되지 않은 경우")
    void t6() throws Exception {
        String title = "제목입니다.";
        String content = "";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-1"))
                .andExpect(jsonPath("$.msg").value("content-NotBlank-02-content-내용은 필수입니다.\n" +
                        "content-Size-04-content-내용은 2자 이상 100자 이하로 입력해주세요."));
    }

    @Test
    @DisplayName("글 작성, JSON 양식이 잘못된 경우")
    void t7() throws Exception {
        String title = "제목입니다.";
        String content = "내용입니다";

        ResultActions resultActions = mvc
                .perform(
                        post("/api/v1/posts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s"
                                            "content": "%s"
                                        
                                        """.formatted(title, content))
                )
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("write"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("400-2"))
                .andExpect(jsonPath("$.msg").value("잘못된 형식의 요청 데이터입니다."));
    }

    @Test
    @DisplayName("글 수정")
    void t8() throws Exception {
        int targetId = 1;
        String title = "제목 수정";
        String content = "내용 수정";
        String apiKey = "user1";

        ResultActions resultActions = mvc
                .perform(
                        put("/api/v1/posts/%d".formatted(targetId))
                                .header("Authorization", apiKey)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "%s",
                                            "content": "%s"
                                        }
                                        """.formatted(title, content))
                )
                .andDo(print());

        // 필수 검증
        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("modify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 게시물이 수정되었습니다.".formatted(targetId)));

        // 선택적 검증
        Post post = postRepository.findById(targetId).get();

        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
    }


    @Test
    @DisplayName("글 삭제")
    void t9() throws Exception{
        String apiKey = "user1";
        int targetId = 1;

        ResultActions resultActions = mvc
                .perform(
                        delete("/api/v1/posts/%d".formatted(targetId))
                        .header("Authorization", apiKey)
                ).andDo(print());

        resultActions
                .andExpect(handler().handlerType(ApiV1PostController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 글이 삭제되었습니다.".formatted(targetId)));
    }



}
