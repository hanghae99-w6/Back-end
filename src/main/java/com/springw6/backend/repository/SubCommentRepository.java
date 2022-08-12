package com.springw6.backend.repository;

import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.SubComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubCommentRepository extends JpaRepository<SubComment, Long> {
  List<SubComment> findAllByCommentId(Long commentId);
  List<SubComment> findAllByMember(Member member);
  Optional<SubComment> findById(Long id);
  int countAllByCommentId(Long id);
}
