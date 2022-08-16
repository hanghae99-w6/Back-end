package com.springw6.backend.service;

import com.springw6.backend.controller.request.CommentRequestDto;
import com.springw6.backend.controller.response.CommentResponseDto;
import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.controller.response.SubCommentResponseDto;
import com.springw6.backend.domain.*;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.CommentRepository;
import com.springw6.backend.repository.SubCommentRepository;
//import com.springw6.backend.repository.CommentLikeRepository;
//import com.springw6.backend.repository.SubCommentLikeRepository;
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
public class CommentService {

  private final CommentRepository commentRepository;
  private final SubCommentRepository subCommentRepository;
//  private final CommentLikeRepository commentLikeRepository;
//  private final SubCommentLikeRepository subCommentLikeRepository;

  private final TokenProvider tokenProvider;
  private final PostService postService;

  @Transactional
  public ResponseEntity<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "refresh token is invalid"), HttpStatus.UNAUTHORIZED);
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "post id is not exist"), HttpStatus.NOT_FOUND);
    }

    Comment comment = Comment.builder()
        .member(member)
        .post(post)
        .comment(requestDto.getComment())
        .build();
    commentRepository.save(comment);

    return new ResponseEntity<>(Message.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .postId(comment.getPost().getId())
            .author(comment.getMember().getNickname())
            .comment(comment.getComment())
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    )
    ,HttpStatus.OK);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<?> getAllCommentsByPost(Long postId) {
    Post post = postService.isPresentPost(postId);
    if (null == post) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "post id is not exist"), HttpStatus.NOT_FOUND);
    }

    List<Comment> commentList = commentRepository.findAllByPost(post);
    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

    for (Comment comment : commentList) {

      // 대댓글 리스트
      List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
      List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();

      for (SubComment subComment : subCommentList) {
        subCommentResponseDtoList.add(
                SubCommentResponseDto.builder()
                        .id(subComment.getId())
                        .author(subComment.getMember().getNickname())
                        .subComment(subComment.getSubComment())
                        .likes(subComment.getLikes())
                        .createdAt(subComment.getCreatedAt())
                        .modifiedAt(subComment.getModifiedAt())
                        .build()
        );
      }

      commentResponseDtoList.add(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .author(comment.getMember().getNickname())
                      .comment(comment.getComment())
                      .subComment(subCommentResponseDtoList) // 여기에 대댓글 넣기
                      .likes(comment.getLikes())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      );
    }
    return new ResponseEntity<>(Message.success(commentResponseDtoList),HttpStatus.OK);
  }

  //멤버별 코멘트 불러오기
//  @Transactional(readOnly = true)
//  public ResponseEntity<?> getAllCommentsByMember(HttpServletRequest request) {
//    Member member = validateMember(request);
//    if (null == member) {
//      return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "refresh token is invalid"), HttpStatus.UNAUTHORIZED);
//    }
//
//    List<Comment> commentList = commentRepository.findAllByMember(member);
//    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//    for (Comment comment : commentList) {
//      commentResponseDtoList.add(
//          CommentResponseDto.builder()
//              .id(comment.getId())
//              .author(comment.getMember().getNickname())
//              .comment(comment.getComment())
////              .likes(countLikesComment(comment))
//              .createdAt(comment.getCreatedAt())
//              .modifiedAt(comment.getModifiedAt())
//              .build()
//      );
//    }
//    return new ResponseEntity<>(Message.success(commentResponseDtoList),HttpStatus.OK);
//  }

  @Transactional
  public ResponseEntity<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "refresh token is invalid"), HttpStatus.UNAUTHORIZED);
    }

    Post post = postService.isPresentPost(requestDto.getPostId());
    if (null == post) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "post id is not exist"), HttpStatus.NOT_FOUND);
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "comment id is not exist"),HttpStatus.NOT_FOUND);
    }

    if (comment.validateMember(member)) {
      return new ResponseEntity<>(Message.fail("BAD_REQUEST", "only author can update"),HttpStatus.BAD_REQUEST);
    }

    List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
    List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
    for (SubComment subComment : subCommentList) {
      subCommentResponseDtoList.add(
          SubCommentResponseDto.builder()
              .id(subComment.getId())
              .subComment(subComment.getSubComment())
              .author(subComment.getMember().getNickname())
//              .likes(countLikesSubCommentLike(subComment))
              .createdAt(subComment.getCreatedAt())
              .modifiedAt(subComment.getModifiedAt())
              .build()
      );
    }

    comment.update(requestDto);
    return new ResponseEntity<>(Message.success(
        CommentResponseDto.builder()
            .id(comment.getId())
            .author(comment.getMember().getNickname())
            .comment(comment.getComment())
//            .likes(countLikesComment(comment))
            .subComment(subCommentResponseDtoList)
            .createdAt(comment.getCreatedAt())
            .modifiedAt(comment.getModifiedAt())
            .build()
    )
            ,HttpStatus.OK);
  }




  @Transactional
  public ResponseEntity<?> deleteComment(Long id, HttpServletRequest request) {
    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "refresh token is invalid"), HttpStatus.UNAUTHORIZED);
    }

    Comment comment = isPresentComment(id);
    if (null == comment) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "comment id is not exist"),HttpStatus.NOT_FOUND);
    }

    if (comment.validateMember(member)) {
      return new ResponseEntity<>(Message.fail("BAD_REQUEST", "only author can update"),HttpStatus.BAD_REQUEST);
    }

    List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
    for (SubComment subComment : subCommentList) {
      subCommentRepository.delete(subComment);
    }

    commentRepository.delete(comment);
    return new ResponseEntity<>(Message.success("success"),HttpStatus.OK);
  }
//
//  @Transactional(readOnly = true)
//  public int countLikesComment(Comment comment) {
//    List<CommentLike> commentLikeList = commentLikeRepository.findAllByComment(comment);
//    return commentLikeList.size();
//  }
//
//  @Transactional(readOnly = true)
//  public int countLikesSubCommentLike(SubComment subComment) {
//    List<SubCommentLike> subCommentLikeList = subCommentLikeRepository.findAllBySubComment(subComment);
//    return subCommentLikeList.size();
//  }

  @Transactional(readOnly = true)
  public Comment isPresentComment(Long id) {
    Optional<Comment> optionalComment = commentRepository.findById(id);
    return optionalComment.orElse(null);
  }

  @Transactional
  public Member validateMember(HttpServletRequest request) {
    if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
      return null;
    }
    return tokenProvider.getMemberFromAuthentication();
  }
}
