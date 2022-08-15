package com.springw6.backend.repository;


import com.springw6.backend.domain.Likes;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LikesRepository extends JpaRepository<Likes,Long> {

    Long countAllByPostId(Long postId);
    Long countAllByCommentId(Long commentId);
    Long countAllBySubCommentId(Long subCommentId);


}
