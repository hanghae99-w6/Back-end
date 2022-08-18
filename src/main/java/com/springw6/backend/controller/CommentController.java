package com.springw6.backend.controller;

import com.springw6.backend.controller.request.CommentRequestDto;
import com.springw6.backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Validated
@RequiredArgsConstructor
@RestController
public class CommentController {

  private final CommentService commentService;

  @RequestMapping(value = "/auth/comment", method = RequestMethod.POST)
  public ResponseEntity<?> createComment(@RequestBody CommentRequestDto requestDto,
                                         HttpServletRequest request) {
    return commentService.createComment(requestDto, request);
  }


  @RequestMapping(value = "/comment/{postId}", method = RequestMethod.GET)
  public ResponseEntity<?> getAllComments(@PathVariable Long postId ) {
    return commentService.getAllCommentsByPost(postId);
  }

  @RequestMapping(value = "/auth/comment/{id}", method = RequestMethod.PUT)
  public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentRequestDto requestDto,
                                      HttpServletRequest request) {
    return commentService.updateComment(id, requestDto, request);
  }

  @RequestMapping(value = "/auth/comment/{id}", method = RequestMethod.DELETE)
  public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                      HttpServletRequest request) {
    return commentService.deleteComment(id, request);
  }
}