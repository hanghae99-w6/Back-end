package com.springw6.backend.repository;


import java.util.Optional;
import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByMember(Member member);
}
