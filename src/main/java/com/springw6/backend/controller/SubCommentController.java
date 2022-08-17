package com.springw6.backend.controller;

import com.springw6.backend.controller.request.SubCommentRequestDto;
import com.springw6.backend.service.SubCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class SubCommentController {

  private final SubCommentService subCommentService;

  @RequestMapping(value = "/auth/subcomment", method = RequestMethod.POST)
  public ResponseEntity<?> createComment(@RequestBody SubCommentRequestDto requestDto,
                                         HttpServletRequest request) {
    return subCommentService.createSubComment(requestDto, request);
  }

  @RequestMapping(value = "/auth/subcomment/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateSubComment(
      @PathVariable Long id,
      @RequestBody SubCommentRequestDto requestDto,
      HttpServletRequest request) {
    return subCommentService.updateSubComment(id, requestDto, request);
  }

  @RequestMapping(value = "/auth/subcomment/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> createComment(@PathVariable Long id,
      HttpServletRequest request) {
    return subCommentService.deleteSubComment(id, request);
  }

  @RequestMapping(value = "/auth/subcomment", method = RequestMethod.GET)
  public ResponseEntity<?> getAllPostByMember(HttpServletRequest request) {
    return subCommentService.getAllSubCommentByMember(request);
  }
}
