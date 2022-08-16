package com.springw6.backend.controller;


import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/auth/postlikes/{id}")
    public ResponseEntity<?> postLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.postLikes(id,request);
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
