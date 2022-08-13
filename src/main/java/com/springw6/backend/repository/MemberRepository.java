package com.springw6.backend.repository;


import com.springw6.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    Optional<Member> findByNickname(String nickname);

    Optional<Member> findByLoginId(String loginId);

    Optional<Member> findByKakaoId(Long kakaoId);

}
