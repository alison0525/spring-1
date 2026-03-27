package com.back.domain.post.service;

import com.back.domain.member.entity.Member;
import com.back.domain.post.entity.Post;
import com.back.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post write(Member author, String title, String content) {
        Post post = new Post(author,title, content);
        return postRepository.save(post);
    }

    @Transactional
    public Post modify(int id, String title, String content) {
        Post post = postRepository.findById(id).get();
        post.update(title, content);

        return post;
    }

    public Optional<Post> findById(int id) {
        return postRepository.findById(id);
    }

    public long count() {
        return postRepository.count();
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public void deleteById(int id) {
        postRepository.deleteById(id);
    }

    public void flush(){
        postRepository.flush();
    }
}