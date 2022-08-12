package com.springw6.backend.controller;

import com.springw6.backend.controller.request.SubCommentRequestDto;
import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.service.SubCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class SubCommentController {

  private final SubCommentService subCommentService;

  @RequestMapping(value = "/api/auth/sub-comment", method = RequestMethod.POST)
  public ResponseDto<?> createComment(@RequestBody SubCommentRequestDto requestDto,
      HttpServletRequest request) {
    return subCommentService.createSubComment(requestDto, request);
  }

  @RequestMapping(value = "/api/auth/sub-comment/{id}", method = RequestMethod.POST)
  public ResponseDto<?> updateSubComment(
      @PathVariable Long id,
      @RequestBody SubCommentRequestDto requestDto,
      HttpServletRequest request) {
    return subCommentService.updateSubComment(id, requestDto, request);
  }

  @RequestMapping(value = "/api/auth/sub-comment/{id}", method = RequestMethod.DELETE)
  public ResponseDto<?> createComment(@PathVariable Long id,
      HttpServletRequest request) {
    return subCommentService.deleteSubComment(id, request);
  }

  @RequestMapping(value = "/api/auth/sub-comment", method = RequestMethod.GET)
  public ResponseDto<?> getAllPostByMember(HttpServletRequest request) {
    return subCommentService.getAllSubCommentByMember(request);
  }
}
