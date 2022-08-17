package com.springw6.backend.controller;


import com.springw6.backend.controller.request.PostRequestDto;
import com.springw6.backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class PostController {
  private final PostService postService;

  @RequestMapping(value = "/auth/post", method = RequestMethod.POST)
  public ResponseEntity<?> createPost(@RequestBody PostRequestDto requestDto,
                                   HttpServletRequest request) {
    return postService.createPost(requestDto, request);
  }
  @RequestMapping(value = "/post/{id}", method = RequestMethod.GET)
  public ResponseEntity<?> getPost(@PathVariable Long id) {
    return postService.getPost(id);
  }
  @RequestMapping(value = "/post", method = RequestMethod.GET)
  public ResponseEntity<?> getAllPosts() {
    return postService.getAllPost();
  }
  @RequestMapping(value = "/main/{category}", method = RequestMethod.GET)
  public ResponseEntity<?> getPost(@PathVariable String category) {
    return postService.getPost(category);
  }

  @RequestMapping(value = "/auth/post/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
      HttpServletRequest request) {
    return postService.updatePost(id, postRequestDto, request);
  }
  @RequestMapping(value = "/auth/post/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }
}
