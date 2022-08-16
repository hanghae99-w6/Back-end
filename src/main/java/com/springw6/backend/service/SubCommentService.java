package com.springw6.backend.service;

import com.springw6.backend.controller.request.SubCommentRequestDto;
import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.controller.response.SubCommentResponseDto;
import com.springw6.backend.domain.*;
import com.springw6.backend.exceptions.CommentNotFoundException;
import com.springw6.backend.exceptions.InvalidTokenException;
import com.springw6.backend.exceptions.NotAuthorException;
import com.springw6.backend.exceptions.SubCommentNotFoundException;
import com.springw6.backend.jwt.TokenProvider;
//import com.springw6.backend.domain.SubCommentLike;
//import com.springw6.backend.repository.SubCommentLikeRepository;
import com.springw6.backend.repository.SubCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubCommentService {

  private final SubCommentRepository subCommentRepository;
//  private final SubCommentLikeRepository subCommentLikeRepository;

  private final TokenProvider tokenProvider;
  private final CommentService commentService;
  private final PostService postService;

  @Transactional
  public ResponseEntity<?> createSubComment(
      SubCommentRequestDto requestDto,
      HttpServletRequest request
  ) {
    Member member = validateMember(request);
    if (null == member) {
      throw new InvalidTokenException();
    }

    Comment comment = commentService.isPresentComment(requestDto.getCommentId());
    if (null == comment) throw new CommentNotFoundException();
    Post post = postService.isPresentPost(comment.getPost().getId());
    SubComment subComment = SubComment.builder()
        .commentId(requestDto.getCommentId())
        .member(member)
        .post(post)
        .comment(comment)
        .subComment(requestDto.getSubComment())
        .likes(0L)
        .build();
    subCommentRepository.save(subComment);
    return new ResponseEntity<>(Message.success(
        SubCommentResponseDto.builder()
          .id(subComment.getId())
          .author(member.getNickname())
          .subComment(subComment.getSubComment())
          .likes(subComment.getLikes())
          .createdAt(subComment.getCreatedAt())
          .modifiedAt(subComment.getModifiedAt())
          .build()
    ),HttpStatus.OK);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<?> getAllSubCommentByMember(HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      throw new InvalidTokenException();
    }

    List<SubComment> subCommentList = subCommentRepository.findAllByMember(member);
    List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();

    for (SubComment subComment : subCommentList) {
      subCommentResponseDtoList.add(
          SubCommentResponseDto.builder()
              .id(subComment.getId())
              .author(subComment.getMember().getNickname())
              .subComment(subComment.getSubComment())
//              .likes(countLikesSubCommentLike(subComment))
              .createdAt(subComment.getCreatedAt())
              .modifiedAt(subComment.getModifiedAt())
              .build()
      );
    }
    return new ResponseEntity<>(Message.success(subCommentResponseDtoList),HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<?> updateSubComment(
      Long id,
      SubCommentRequestDto requestDto,
      HttpServletRequest request
  ) {
    Member member = validateMember(request);
    if (null == member) {
      throw new InvalidTokenException();
    }

    Comment comment = commentService.isPresentComment(requestDto.getCommentId());
    if (null == comment) throw new CommentNotFoundException();

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment) {
      throw new SubCommentNotFoundException();
    }

    if (subComment.validateMember(member)) {
      throw new NotAuthorException();
    }

    subComment.update(requestDto);
    return new ResponseEntity<>(Message.success(
        SubCommentResponseDto.builder()
            .id(subComment.getId())
            .author(member.getNickname())
            .subComment(subComment.getSubComment())
            .createdAt(subComment.getCreatedAt())
            .modifiedAt(subComment.getModifiedAt())
            .build()
    ),HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<?> deleteSubComment(
      Long id,
      HttpServletRequest request
  ) {
    Member member = validateMember(request);
    if (null == member) {
      throw new InvalidTokenException();
    }

    Comment comment = commentService.isPresentComment(id);
    if (null == comment)  throw new CommentNotFoundException();

    SubComment subComment = isPresentSubComment(id);
    if (null == subComment)  throw new SubCommentNotFoundException();

    if (subComment.validateMember(member)) {
      throw new NotAuthorException();
    }

    subCommentRepository.delete(subComment);
    return new ResponseEntity<>(Message.success("success"),HttpStatus.OK);
  }

//  @Transactional(readOnly = true)
//  public int countLikesSubCommentLike(SubComment subComment) {
//    List<SubCommentLike> subCommentLikeList = subCommentLikeRepository.findAllBySubComment(subComment);
//    return subCommentLikeList.size();
//  }

  @Transactional(readOnly = true)
  public SubComment isPresentSubComment(Long id) {
    Optional<SubComment> optionalSubComment = subCommentRepository.findById(id);
    return optionalSubComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }

}
