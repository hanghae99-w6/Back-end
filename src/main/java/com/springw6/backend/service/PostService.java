package com.springw6.backend.service;

import com.springw6.backend.controller.request.PostRequestDto;
import com.springw6.backend.controller.response.*;
import com.springw6.backend.domain.*;
import com.springw6.backend.exceptions.InvalidAccessTokenException;
import com.springw6.backend.exceptions.InvalidTokenException;
import com.springw6.backend.exceptions.NotAuthorException;
import com.springw6.backend.exceptions.PostNotFoundException;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

   private final PostRepository postRepository;
   private final TokenProvider tokenProvider;


   @Transactional
   public ResponseEntity<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {

      if (null == request.getHeader("Refresh-Token")) {
         throw new InvalidTokenException();
      }
      if (null == request.getHeader("Authorization")) {
         throw new InvalidAccessTokenException();
      }
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }

      Post post = Post.builder()
              .title(requestDto.getTitle())
              .content(requestDto.getContent())
              .imgUrl(requestDto.getImgUrl())
              .member(member)
              .likes(0L)
              .star(requestDto.getStar())
              .category(requestDto.getCategory())
              .build();
      postRepository.save(post);
      return new ResponseEntity<>(Message.success(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .author(post.getMember().getNickname())
                      .content(post.getContent())
                      .imgUrl(post.getImgUrl())
                      .star(post.getStar())
                      .category(post.getCategory())
                      .likes(post.getLikes())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      ), HttpStatus.OK);
   }


   @Transactional(readOnly = true)
   public ResponseEntity<?> getPost(Long id) {
      Post post = isPresentPost(id);
      if (null == post) {
         throw new PostNotFoundException();
      }

      return new ResponseEntity<>(Message.success(
              PostResponseDto.builder()
                      .id(post.getId())
                      .title(post.getTitle())
                      .content(post.getContent())
                      .author(post.getMember().getNickname())
                      .star((post.getStar()))
                      .imgUrl(post.getImgUrl())
                      .likes(post.getLikes())
                      .createdAt(post.getCreatedAt())
                      .modifiedAt(post.getModifiedAt())
                      .build()
      ),HttpStatus.OK);
   }

   @Transactional(readOnly = true)
   public ResponseEntity<?> getAllPost() {
      return  new ResponseEntity<>(Message.success(postRepository.findAllByOrderByModifiedAtDesc())
              ,HttpStatus.OK);
   }

   @Transactional(readOnly = true)
   public ResponseEntity<?> getPost(String category) {
      return  new ResponseEntity<>(Message.success(postRepository.findByCategoryOrderByCreatedAtDesc(category))
              ,HttpStatus.OK);
   }


   @Transactional
   public ResponseEntity<?> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
      if (null == request.getHeader("Refresh-Token")) {
         throw new InvalidTokenException();
      }

      if (null == request.getHeader("Authorization")) {
         throw new InvalidAccessTokenException();
      }

      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }

      Post post = isPresentPost(id);
      if (null == post) {
         throw new PostNotFoundException();
      }

      if (post.getMember().getId()!=member.getId()) {
         throw new NotAuthorException();
      }

      post.update(requestDto);
      return new ResponseEntity<>(Message.success(post),HttpStatus.OK);
   }

   @Transactional
   public ResponseEntity<?> deletePost(Long id, HttpServletRequest request) {
      if (null == request.getHeader("Refresh-Token")) {
         throw new InvalidTokenException();
      }

      if (null == request.getHeader("Authorization")) {
         throw new InvalidAccessTokenException();
      }

      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }

      Post post = isPresentPost(id);
      if (null == post) {
         throw new PostNotFoundException();
      }
      if (post.getMember().getId()!=member.getId()) {
         throw new NotAuthorException();
      }

      postRepository.delete(post);
      return new ResponseEntity<>(Message.success("delete success"),HttpStatus.OK);
   }

   @Transactional(readOnly = true)
   public Post isPresentPost(Long id) {
      Optional<Post> optionalPost = postRepository.findById(id);
      return optionalPost.orElse(null);
   }

   @Transactional
   public Member validateMember(HttpServletRequest request) {
      if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
         return null;
      }
      return tokenProvider.getMemberFromAuthentication();
   }

   public void isRefreshTokenCheck(HttpServletRequest request) {
      if (null == request.getHeader("Refresh-Token")) {
        throw new InvalidTokenException();
      }
   }

   public void isAuthorizationCheck(HttpServletRequest request) {
      if (null == request.getHeader("Authorization")) {
         throw new InvalidTokenException();
      }
   }

   public void isTest(HttpServletRequest request) {
      Member member = validateMember(request);
      if (null == member) {
         throw new InvalidTokenException();
      }
   }

}
