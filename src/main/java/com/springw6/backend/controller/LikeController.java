package com.springw6.backend.controller;


import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/auth/postLikes/{id}")
    public ResponseDto<?> postLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.postLikes(id,request);
    }

    @PostMapping("/auth/commentLikes/{id}")
    public ResponseDto<?> commentLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.commentLikes(id,request);
    }

    @PostMapping("/auth/subCommentLikes/{id}")
    public ResponseDto<?> subCommentLikes(@PathVariable Long id, HttpServletRequest request) {
        return likeService.subCommentLikes(id,request);
    }


}
