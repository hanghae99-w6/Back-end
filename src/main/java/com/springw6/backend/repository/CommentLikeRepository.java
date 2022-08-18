package com.springw6.backend.repository;


import com.springw6.backend.domain.Comment;
import com.springw6.backend.domain.Likes;
import com.springw6.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndComment(Member member, Comment comment);
}
