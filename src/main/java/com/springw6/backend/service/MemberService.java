package com.springw6.backend.service;

import com.springw6.backend.domain.Member;
import com.springw6.backend.dto.requestDto.SignupRequestDto;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
public class MemberService {

    @Transactional
    public Member signupMembers(SignupRequestDto signupRequestDto) {
        Member member = new Member();
        return member;
    }

}
