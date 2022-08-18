package com.springw6.backend.service;

import com.springw6.backend.controller.request.SubCommentRequestDto;
import com.springw6.backend.controller.response.SubCommentResponseDto;
import com.springw6.backend.domain.*;
import com.springw6.backend.exceptions.CommentNotFoundException;
import com.springw6.backend.exceptions.InvalidTokenException;
import com.springw6.backend.exceptions.NotAuthorException;
import com.springw6.backend.exceptions.SubCommentNotFoundException;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.SubCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubCommentService {

   private final SubCommentRepository subCommentRepository;

   private final TokenProvider tokenProvider;
   private final CommentService commentService;
   private final PostService postService;

   @Transactional
   public ResponseEntity<?> createSubComment(
           SubCommentRequestDto requestDto,
           HttpServletRequest request
   ) {
      //토큰으로 멤버 정보 가져오고 없으면 예외 처리
      Member member = validateMember(request);
         if (null == member) throw new InvalidTokenException();
      //요청에 들어온 댓글 아이디로 댓글 정보 가져오고 없으면 예외처리
      Comment comment = commentService.isPresentComment(requestDto.getCommentId());
         if (null == comment) throw new CommentNotFoundException();
      //게시글 가져오고, 대댓글을 매핑시키기위해 멤버,댓글,게시글 정보를 추가해줌
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
      ), HttpStatus.OK);
   }

   @Transactional
   public ResponseEntity<?> updateSubComment(
           Long id,
           SubCommentRequestDto requestDto,
           HttpServletRequest request
   ) {
      //토큰으로 멤버 정보 가져오고 없으면 예외 처리
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
      //주소값에서 받아온 대댓글 아이디로 대댓글을 찾아서 돌려주고 없으면 예외처리
      SubComment subComment = isPresentSubComment(id);
      if (null == subComment) {
         throw new SubCommentNotFoundException();
      }
      //대댓글의 작성자와 로그인 된 사용자를 비교하여 다른 경우 예외처리
      if (!subComment.getMember().getId().equals(member.getId())) {
         throw new NotAuthorException();
      }

      subComment.update(requestDto);
      return new ResponseEntity<>(Message.success(
              SubCommentResponseDto.builder()
                      .id(subComment.getId())
                      .author(member.getNickname())
                      .subComment(subComment.getSubComment())
                      .likes(subComment.getLikes())
                      .createdAt(subComment.getCreatedAt())
                      .modifiedAt(subComment.getModifiedAt())
                      .build()
      ), HttpStatus.OK);
   }

   @Transactional
   public ResponseEntity<?> deleteSubComment(
           Long id,
           HttpServletRequest request
   ) {
      //토큰으로 멤버 정보 가져오고 없으면 예외 처리
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
      //주소값에서 받아온 대댓글 아이디로 대댓글을 찾아서 돌려주고 없으면 예외처리
      SubComment subComment = isPresentSubComment(id);
      if (null == subComment) throw new SubCommentNotFoundException();
      //대댓글의 작성자와 로그인 된 사용자를 비교하여 다른 경우 예외처리
      if (!subComment.getMember().getId().equals(member.getId())) {
         throw new NotAuthorException();
      }
      subCommentRepository.delete(subComment);
      return new ResponseEntity<>(Message.success("success"), HttpStatus.OK);
   }

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
