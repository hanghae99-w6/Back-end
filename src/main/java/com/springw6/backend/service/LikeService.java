package com.springw6.backend.service;


import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.domain.*;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;

@Service
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



    @Transactional
    public ResponseDto<?> postLikes(Long id, HttpServletRequest request) {
        Member member =validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "게시글에 좋아요를 누르기 위해서는 로그인을 해 주세요");
        }
        Post post = postService.isPresentPost(id);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "게시글이 존재하지 않습니다.");
        }
        Likes postLike = isPresentPostLike(member, post);
        if (null == postLike) {
            postLikeRepository.save(Likes.builder()
                    .member(member)
                    .post(post)
                    .build()
            );
            Long likes = likesRepository.countAllByPostId(post.getId());
            post.updateLikes(likes);
            return ResponseDto.success("좋아요를 누르셨습니다.");
        } else {
            postLikeRepository.delete(postLike);
            Long likes = likesRepository.countAllByPostId(post.getId());
            post.updateLikes(likes);
            return ResponseDto.success("좋아요를 취소하셨습니다.");
        }

    }


    @Transactional
    public ResponseDto<?> commentLikes(Long id, HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "댓글에 좋아요를 누르기 위해서는 로그인을 해 주세요");
        }

        Comment comment = commentService.isPresentComment(id);
        if (null == comment) {
            return ResponseDto.fail("NOT_FOUND", "댓글이 존재하지 않습니다.");
        }
        Likes commentLike = isPresentCommentLike(member, comment);
        if (null == commentLike) {
            commentLikeRepository.save(Likes.builder()
                    .member(member)
                    .comment(comment)
                    .build()
            );
            Long likes = likesRepository.countAllByCommentId(comment.getId());
            comment.updateLikes(likes);
            return ResponseDto.success("좋아요를 누르셨습니다.");
        } else {
            commentLikeRepository.delete(commentLike);
            Long likes = likesRepository.countAllByCommentId(comment.getId());
            comment.updateLikes(likes);
            return ResponseDto.success("좋아요를 취소하셨습니다.");
        }
    }


    @Transactional
    public ResponseDto<?> subCommentLikes(Long id, HttpServletRequest request) {
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "대댓글에 좋아요를 누르기 위해서는 로그인을 해 주세요");
        }

        SubComment subComment = subCommentService.isPresentSubComment(id);
        if (null == subComment) {
            return ResponseDto.fail("NOT_FOUND", "대댓글이 존재하지 않습니다.");
        }

        Likes subCommentLike = isPresentSubCommentLike(member, subComment);
        if (null == subCommentLike) {
            subCommentLikeRepository.save(Likes.builder()
                    .member(member)
                    .subComment(subComment)
                    .build()
            );
            Long likes = likesRepository.countAllBySubCommentId(subComment.getId());
            subComment.updateLikes(likes);
            return ResponseDto.success("좋아요를 누르셨습니다.");
        } else {
            subCommentLikeRepository.delete(subCommentLike);
            Long likes = likesRepository.countAllBySubCommentId(subComment.getId());
            subComment.updateLikes(likes);
            return ResponseDto.success("좋아요를 취소하셨습니다.");
        }
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
    public int countLikesPost(Post post) {
        List<Likes> postLikeList = postLikeRepository.findAllByPost(post);
        return postLikeList.size();
    }


    @Transactional(readOnly = true)
    public int countLikesComment(Comment comment) {
        List<Likes> commentLikeList = commentLikeRepository.findAllByComment(comment);
        return commentLikeList.size();
    }

    @Transactional(readOnly = true)
    public int countLikesSubCommentLike(SubComment subComment) {
        List<Likes> subCommentLikeList = subCommentLikeRepository.findAllBySubComment(subComment);
        return subCommentLikeList.size();
    }



}
