package com.springw6.backend.service;


import com.springw6.backend.domain.*;
import com.springw6.backend.exceptions.CommentNotFoundException;
import com.springw6.backend.exceptions.InvalidTokenException;
import com.springw6.backend.exceptions.PostNotFoundException;
import com.springw6.backend.exceptions.SubCommentNotFoundException;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.*;
import lombok.Getter;
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
@Getter
@RequiredArgsConstructor
public class LikeService {

   private final TokenProvider tokenProvider;
   private final PostService postService;
   private final PostLikeRepository postLikeRepository;
   private final CommentService commentService;
   private final CommentLikeRepository commentLikeRepository;
   private final SubCommentService subCommentService;
   private final SubCommentLikeRepository subCommentLikeRepository;
   private final LikesRepository likesRepository;

   //게시글에 좋아요를 누를 수 있는 함수
   @Transactional
   public ResponseEntity<?> postLikes(Long id, HttpServletRequest request) {
      //로그인 확인
      Member member = getMember(request);
      //포스트 확인
      Post post = getPost(id);
      //게시글에 좋아요를 눌렸는지 확인,
      // 그냥 게시글에서 확인하는 것이 아니라 포스트 라이크 리포지토리를 만들고, 좋아요를 누른 게시글에서 찾음
      Likes postLike = isPresentPostLike(member, post);
      //안눌렀으면, 멤버와 게시글 아이디를 저장
      if (null == postLike) {
         postLikeRepository.save(Likes.builder()
                 .member(member)
                 .post(post)
                 .build()
         );
         //게시글의 총 좋아요 개수 확인 후 업데이트
         Long likes = likesRepository.countAllByPostId(post.getId());
         post.updateLikes(likes);
         return new ResponseEntity<>(Message.success("좋아요를 눌렀습니다."), HttpStatus.OK);
      } else {
         //한번 더 누르면 토글 형식으로 삭제 가능
         postLikeRepository.delete(postLike);
         Long likes = likesRepository.countAllByPostId(post.getId());
         post.updateLikes(likes);
         return new ResponseEntity<>(Message.success("좋아요를 취소했습니다."), HttpStatus.OK);
      }

   }

   //댓글에 좋아요를 누를 수 있는 함수
   @Transactional
   public ResponseEntity<?> commentLikes(Long id, HttpServletRequest request) {
      //로그인 확인
      Member member = getMember(request);
      //댓글 확인
      Comment comment = getComment(id);
      //댓글에 좋아요 없으면 저장
      Likes commentLike = isPresentCommentLike(member, comment);
      if (null == commentLike) {
         commentLikeRepository.save(Likes.builder()
                 .member(member)
                 .comment(comment)
                 .build()
         );
         //댓글의 총 좋아요 개수 확인 후 업데이트
         Long likes = likesRepository.countAllByCommentId(comment.getId());
         comment.updateLikes(likes);
         //한번 누르면 저장
         return new ResponseEntity<>(Message.success("좋아요를 눌렀습니다."), HttpStatus.OK);
      } else {
         //한번 더 누르면 삭제
         commentLikeRepository.delete(commentLike);
         Long likes = likesRepository.countAllByCommentId(comment.getId());
         comment.updateLikes(likes);
         return new ResponseEntity<>(Message.success("좋아요를 취소했습니다."), HttpStatus.OK);
      }
   }

   @Transactional
   public ResponseEntity<?> subCommentLikes(Long id, HttpServletRequest request) {
      Member member = getMember(request);
      SubComment subComment = getSubComment(id);
      Likes subCommentLike = isPresentSubCommentLike(member, subComment);
      if (null == subCommentLike) {
         subCommentLikeRepository.save(Likes.builder()
                 .member(member)
                 .subComment(subComment)
                 .build()
         );
         Long likes = likesRepository.countAllBySubCommentId(subComment.getId());
         subComment.updateLikes(likes);
         return new ResponseEntity<>(Message.success("좋아요를 눌렀습니다."), HttpStatus.OK);
      } else {
         subCommentLikeRepository.delete(subCommentLike);
         Long likes = likesRepository.countAllBySubCommentId(subComment.getId());
         subComment.updateLikes(likes);
         return new ResponseEntity<>(Message.success("좋아요를 취소했습니다."), HttpStatus.OK);
      }
   }

   private Post getPost(Long id) {
      Post post = postService.isPresentPost(id);
      if (null == post) {
         throw new PostNotFoundException();
      }
      return post;
   }
   private Member getMember(HttpServletRequest request) {
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
      return member;
   }
   private Comment getComment(Long id) {
      Comment comment = commentService.isPresentComment(id);
      if (null == comment) {
         throw new CommentNotFoundException();
      }
      return comment;
   }
   private SubComment getSubComment(Long id) {
      SubComment subComment = subCommentService.isPresentSubComment(id);
      if (null == subComment) {
         throw new SubCommentNotFoundException();
      }
      return subComment;
   }

   @Transactional
   public Member validateMember(HttpServletRequest request) {
      if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
         return null;
      }
      return tokenProvider.getMemberFromAuthentication();
   }

   @Transactional(readOnly = true)
   public Likes isPresentPostLike(Member member, Post post) {
      Optional<Likes> optionalPostLike = postLikeRepository.findByMemberAndPost(member, post);
      return optionalPostLike.orElse(null);
   }

   @Transactional(readOnly = true)
   public Likes isPresentCommentLike(Member member, Comment comment) {
      Optional<Likes> optionalCommentLike = commentLikeRepository.findByMemberAndComment(member, comment);
      return optionalCommentLike.orElse(null);
   }

   @Transactional(readOnly = true)
   public Likes isPresentSubCommentLike(Member member, SubComment subComment) {
      Optional<Likes> optionalSubCommentLike =
              subCommentLikeRepository.findByMemberAndSubComment(member, subComment);
      return optionalSubCommentLike.orElse(null);
   }
   @Transactional(readOnly = true)
   public List<Long> likedPostList(HttpServletRequest request) {
      Member member = validateMember(request);
      List<Long> list=new ArrayList<>();
      for (Likes post : postLikeRepository.findAllByMemberOrderByPost(member)) {
         list.add(post.getPost().getId());
      }
      return list;
   }
}
