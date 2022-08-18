package com.springw6.backend.repository;

import com.springw6.backend.domain.Likes;
import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubCommentLikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndSubComment(Member member, SubComment subComment);
}
