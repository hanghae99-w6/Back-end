package com.springw6.backend.controller;

import com.springw6.backend.repository.PostLikeRepository;
import com.springw6.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class LikeController {
    private final LikeService likeService;
    private final PostLikeRepository postLikeRepository;

    @PostMapping("/auth/postlikes/{id}")
    public ResponseEntity<?> postLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.postLikes(id,request);
    }
    @GetMapping("/auth/likedpost")
    public List<Long> likedPost(HttpServletRequest request){
       return likeService.likedPostList(request);
    }

    @PostMapping("/auth/commentlikes/{id}")
    public ResponseEntity<?> commentLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.commentLikes(id,request);
    }

    @PostMapping("/auth/subcommentlikes/{id}")
    public ResponseEntity<?> subCommentLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.subCommentLikes(id,request);
    }


}
