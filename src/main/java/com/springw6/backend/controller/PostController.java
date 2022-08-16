package com.springw6.backend.controller;


import com.springw6.backend.controller.request.PostRequestDto;
import com.springw6.backend.controller.response.ResponseDto;
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

  @RequestMapping(value = "/auth/post/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody PostRequestDto postRequestDto,
      HttpServletRequest request) {
    return postService.updatePost(id, postRequestDto, request);
  }


  //Delete 부분은 이전 형식 그대로 ,  아직 ResponseEntity형식으로 바꾸어 주지 않았다.
  // Exception에서 알아서 다 처리 하므로, 변경을 하지 않았는데, 추후에 깔끔하게 다시 정리 할것.
  // 설계 과정의 차이를 알아보기 위해서, 변경전의 이전 형식을 그대로~~
  @RequestMapping(value = "/auth/post/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> deletePost(@PathVariable Long id,
      HttpServletRequest request) {
    return postService.deletePost(id, request);
  }

}
