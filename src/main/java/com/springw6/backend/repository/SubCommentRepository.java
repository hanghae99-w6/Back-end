package com.springw6.backend.repository;

import com.springw6.backend.domain.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
  List<SubComment> findAllByCommentId(Long commentId);
}
