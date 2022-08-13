package com.springw6.backend.controller;

import com.springw6.backend.dto.responseDto.ResponseDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MemberController {

    @PostMapping("/members/signup")
    public ResponseDto<?> signupMembers() {

        return signupMembers();
    }

}
