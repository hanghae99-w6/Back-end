package com.springw6.backend.service;

import com.springw6.backend.controller.request.PostRequestDto;
import com.springw6.backend.controller.response.*;
import com.springw6.backend.domain.*;
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
  private final CommentRepository commentRepository;
  private final TokenProvider tokenProvider;


  @Transactional
  public ResponseEntity<?> createPost(PostRequestDto requestDto, HttpServletRequest request) {
//    isRefreshTokenCheck(request);
//    isAuthorizationCheck(request);
//    isTest(request);

    if (null == request.getHeader("Refresh-Token")) {
      return new ResponseEntity<>(Message.fail("MEMBER_NOT_FOUND","로그인이 필요합니다"), HttpStatus.NOT_FOUND);
    }
    if (null == request.getHeader("Authorization")) {
      return new ResponseEntity<>(Message.fail("No_Authorization", "로그인이 필요합니다"), HttpStatus.UNAUTHORIZED);
    }
      Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "Token이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
    }

    Post post = Post.builder()
            .title(requestDto.getTitle())
            .content(requestDto.getContent())
            .imgUrl(requestDto.getImgUrl())
            .member(member)
            .star(requestDto.getStar())
            .category(requestDto.getCategory())
            .build();
    postRepository.save(post);
    return new ResponseEntity<>(Message.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
                    .imgUrl(post.getImgUrl())
                    .star(post.getStar())
                    .category(post.getCategory())
                    .likes(post.getLikes())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    )
    , HttpStatus.OK);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<?> getPost(Long id) {
    Post post = isPresentPost(id);
    if (null == post) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다."),HttpStatus.NOT_FOUND);
    }

//    List<Comment> commentList = commentRepository.findAllByPost(post);
//
//    System.out.println("[게시글 조회] 해당 게시물의 댓글 리스트 (commentList): " + commentList);
//
//    List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
//
//    for (Comment comment : commentList) {
//      commentResponseDtoList.add(
//                              CommentResponseDto.builder()
//                                      .id(comment.getId())
////                                      .author(comment.getMember().getNickname())
////                      .content(comment.getContent())
//                                      .likes(comment.getLikes())
//                                      .createdAt(comment.getCreatedAt())
//                      .modifiedAt(comment.getModifiedAt())
//                      .build()
//      );
//    }
//
//    System.out.println("[게시글 조회] 해당 게시물의 댓글 리스트 DTO (commentResponseDtoList): " + commentResponseDtoList);

    return new ResponseEntity<>(Message.success(
            PostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .content(post.getContent())
//                    .commentResponseDtoList(commentResponseDtoList)
                    .star((post.getStar()))
                    .imgUrl(post.getImgUrl())
                    .likes(post.getLikes())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build()
    )
            ,HttpStatus.OK);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<?> getAllPost() {
    return new ResponseEntity<>(Message.success(postRepository.findAllByOrderByModifiedAtDesc()), HttpStatus.OK);
  }

  @Transactional
  public ResponseEntity<?> updatePost(Long id, PostRequestDto requestDto, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return new ResponseEntity<>(Message.fail("Refresh_Token is not invalid", "로그인이 필요합니다."),HttpStatus.UNAUTHORIZED);
    }

    if (null == request.getHeader("Authorization")) {
      return new ResponseEntity<>(Message.fail("No_Authorization","로그인이 필요합니다."),HttpStatus.UNAUTHORIZED);
    }

    Member member = validateMember(request);
    if (null == member) {
      return new ResponseEntity<>(Message.fail("INVALID_TOKEN", "Token이 유효하지 않습니다."),HttpStatus.UNAUTHORIZED);
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return new ResponseEntity<>(Message.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다."),HttpStatus.NOT_FOUND);
    }

    if (post.validateMember(member)) {
      return new ResponseEntity<>(Message.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다."),HttpStatus.UNAUTHORIZED);
    }

    post.update(requestDto);
    return new ResponseEntity<>(Message.success(post),HttpStatus.OK);
  }



  // DeletePOST는 이전 형식 그대로 , 아직 ResponseEntity 형태로 바꾸지 않았음...
  @Transactional
  public ResponseDto<?> deletePost(Long id, HttpServletRequest request) {
    if (null == request.getHeader("Refresh-Token")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    if (null == request.getHeader("Authorization")) {
      return ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다.");
    }

    Member member = validateMember(request);
    if (null == member) {
      return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }

    Post post = isPresentPost(id);
    if (null == post) {
      return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
    }

    if (post.validateMember(member)) {
      return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
    }

    postRepository.delete(post);
    return ResponseDto.success("delete success");
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
      ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다");
    }
  }

  public void isAuthorizationCheck(HttpServletRequest request) {
    if (null == request.getHeader("Authorization")) {
      ResponseDto.fail("MEMBER_NOT_FOUND",
              "로그인이 필요합니다");
    }
  }
  public void isTest(HttpServletRequest request){
    Member member = validateMember(request);
    if (null == member) {
      ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
    }
  }

}