package com.springw6.backend.service;

import com.springw6.backend.controller.request.CommentRequestDto;
import com.springw6.backend.controller.response.CommentResponseDto;
import com.springw6.backend.controller.response.SubCommentResponseDto;
import com.springw6.backend.domain.*;
import com.springw6.backend.exceptions.CommentNotFoundException;
import com.springw6.backend.exceptions.InvalidTokenException;
import com.springw6.backend.exceptions.NotAuthorException;
import com.springw6.backend.exceptions.PostNotFoundException;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.CommentRepository;
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
public class CommentService {

   private final CommentRepository commentRepository;
   private final SubCommentRepository subCommentRepository;
   private final TokenProvider tokenProvider;
   private final PostService postService;

   @Transactional
   public ResponseEntity<?> createComment(CommentRequestDto requestDto, HttpServletRequest request) {
      //request에서 받은 토큰 정보에서 멤버 정보 불러오고 없는 경우 예외 반환
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
      //requestDto에서 받은 정보로 포스트 정보 불러오고 없는 경우 예외 반환
      Post post = postService.isPresentPost(requestDto.getPostId());
      if (null == post) {
         throw new PostNotFoundException();
      }
      //빌더 패턴을 이용해 comment 생성 후 저장
      Comment comment = Comment.builder()
              .member(member)
              .post(post)
              .comment(requestDto.getComment())
              .likes(0L)
              .build();
      commentRepository.save(comment);
      //빌더패턴으로 결과값을 CommentResponseDto의 형태를 생성후 ResponseEntity<>에 담아 200 OK로 반환
      return new ResponseEntity<>(Message.success(
              CommentResponseDto.builder()
                      .id(comment.getId())
                      .postId(comment.getPost().getId())
                      .author(comment.getMember().getNickname())
                      .likes(0L)
                      .comment(comment.getComment())
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      )
              , HttpStatus.OK);
   }

   @Transactional(readOnly = true)
   public ResponseEntity<?> getAllCommentsByPost(Long postId) {
      //requestDto에서 받은 정보로 포스트 정보 불러오고 없는 경우 예외 반환
      Post post = postService.isPresentPost(postId);
      if (null == post) {
         throw new PostNotFoundException();
      }
      //Post에 달린 Comment를 담은 commentList생성
      List<Comment> commentList = commentRepository.findAllByPost(post);
      //결과를 담을 commentResponseDtoList 생성
      List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
      //commentList의 모든 값에 대해 반복문
      for (Comment comment : commentList) {
         // 각 댓글의 아이디를 통해 찾은 대댓글 리스트와 대댓글 결과를 담을 리스트 생성
         List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
         List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
         //대댓글을 반복문을 통해 빌더 패턴으로 넣어줌
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
         //위에서 만들어진 대댓글 리스트를 담아 빌더 패턴으로 댓글을 반환
         commentResponseDtoList.add(
                 CommentResponseDto.builder()
                         .id(comment.getId())
                         .postId(postId)
                         .author(comment.getMember().getNickname())
                         .comment(comment.getComment())
                         .subComment(subCommentResponseDtoList)
                         .likes(comment.getLikes())
                         .createdAt(comment.getCreatedAt())
                         .modifiedAt(comment.getModifiedAt())
                         .build()
         );
      }
      return new ResponseEntity<>(Message.success(commentResponseDtoList), HttpStatus.OK);
   }


   @Transactional
   public ResponseEntity<?> updateComment(Long id, CommentRequestDto requestDto, HttpServletRequest request) {
      //request에서 받은 토큰 정보에서 멤버 정보 불러오고 없는 경우 예외 반환
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
      //requestDto에서 받은 정보로 포스트 정보 불러오고 없는 경우 예외 반환
      Post post = postService.isPresentPost(requestDto.getPostId());
      if (null == post) {
         throw new PostNotFoundException();
      }
      //댓글 정보 조회 후 없는 경우 예외 처리
      Comment comment = isPresentComment(id);
      if (null == comment) {
         throw new CommentNotFoundException();
      }
      //댓글의 작성자를 닉네임을 통해 비교하고 다른 경우 예외 처리
      if (!comment.getMember().getNickname().equals(member.getNickname())) {
         throw new NotAuthorException();
      }
      //댓글 아이디로 대댓글을 찾고 대댓글 만들기 후 업데이트
      List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
      List<SubCommentResponseDto> subCommentResponseDtoList = new ArrayList<>();
      for (SubComment subComment : subCommentList) {
         subCommentResponseDtoList.add(
                 SubCommentResponseDto.builder()
                         .id(subComment.getId())
                         .subComment(subComment.getSubComment())
                         .author(subComment.getMember().getNickname())
                         .likes(subComment.getLikes())
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
                      .likes(comment.getLikes())
                      .subComment(subCommentResponseDtoList)
                      .createdAt(comment.getCreatedAt())
                      .modifiedAt(comment.getModifiedAt())
                      .build()
      )
              , HttpStatus.OK);
   }

   @Transactional
   public ResponseEntity<?> deleteComment(Long id, HttpServletRequest request) {
      //request에서 받은 토큰 정보에서 멤버 정보 불러오고 없는 경우 예외 반환
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
      //댓글이 존재하면 댓글/아닌 경우 예외 처리
      Comment comment = isPresentComment(id);
      if (null == comment) {
         throw new CommentNotFoundException();
      }
      //댓글의 작성자를 닉네임을 통해 비교하고 다른 경우 예외 처리
      if (!comment.getMember().getNickname().equals(member.getNickname())) {
         throw new NotAuthorException();
      }
      //해당 댓글의 대댓글 리스트를 불러오고 대댓글이 있는 경우 삭제 대신 삭제된 댓글이라고 표시
//      List<SubComment> subCommentList = subCommentRepository.findAllByCommentId(comment.getId());
//      if (subCommentList.size()!=0){comment.setComment("삭제된 댓글입니다.");} else{
//            commentRepository.delete(comment);}
      comment.setComment("삭제된 댓글입니다.");
      return new ResponseEntity<>(Message.success("success"), HttpStatus.OK);
   }

   @Transactional(readOnly = true)
   public Comment isPresentComment(Long id) {
      //들어온 아이디에 대한 댓글이 존재하면 댓글을 반환하고 없으면 null반환
      Optional<Comment> optionalComment = commentRepository.findById(id);
      return optionalComment.orElse(null);
   }

   @Transactional
   public Member validateMember(HttpServletRequest request) {
      //토큰이 유효하지 않은 경우 null반환/유효한 경우 토큰에 매칭되는 멤버를 돌려줌
      if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
         return null;
      }
      return tokenProvider.getMemberFromAuthentication();
   }
}
