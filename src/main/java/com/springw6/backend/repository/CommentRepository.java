package com.springw6.backend.repository;


import com.springw6.backend.domain.Comment;
import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  List<Comment> findAllByPost(Post post);
}
