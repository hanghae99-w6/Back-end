package com.springw6.backend.repository;


import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
  List<Post> findAllByOrderByModifiedAtDesc();
  List<Post> findAllByMember(Member member);

}
