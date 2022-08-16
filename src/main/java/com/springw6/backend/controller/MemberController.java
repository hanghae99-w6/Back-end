package com.springw6.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springw6.backend.controller.request.*;
import com.springw6.backend.domain.Message;
import com.springw6.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/members/signup")
    public ResponseEntity<?> signupMembers(@RequestBody SignupRequestDto requestDto) {
        return memberService.signupMember(requestDto);
    }

    @PostMapping("/members/nicknamecheck")
    public ResponseEntity<?> nicknameDubCheck(@RequestBody NicknameCheckRequestDto requestDto) {
        return memberService.nicknameDubCheck(requestDto);
    }

    @PostMapping("/members/idcheck")
    public ResponseEntity<?> idDubCheck(@RequestBody LoginIdCheckRequestDto requestDto) {
        return memberService.loginIdDubCheck(requestDto);
    }

    @PostMapping("/members/login")
    public ResponseEntity<?> loginMembers(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        return memberService.loginMembers(requestDto,response);
    }

    @PostMapping("/members/logout")
    public ResponseEntity<?> logoutMembers(HttpServletRequest request) {
        return memberService.logoutMembers(request);
    }

    @GetMapping("/members/kakao/callback")
    public ResponseEntity<?> kakaoLogin(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        TokenDto tokenDto= memberService.kakaoLogin(code);
        tokenDto.tokenToHeaders(response);
        return new ResponseEntity<>(Message.success("로그인에 성공하였습니다."), HttpStatus.OK);
    }

}