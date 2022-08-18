package com.springw6.backend.repository;

import com.springw6.backend.domain.Likes;
import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<Likes, Long> {
    Optional<Likes> findByMemberAndPost(Member member, Post post);
    List<Likes> findAllByMemberOrderByPost(Member member);

}
