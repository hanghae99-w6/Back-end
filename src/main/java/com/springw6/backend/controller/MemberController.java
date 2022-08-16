package com.springw6.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springw6.backend.controller.request.LoginIdCheckRequestDto;
import com.springw6.backend.controller.request.LoginRequestDto;
import com.springw6.backend.controller.request.NicknameCheckRequestDto;
import com.springw6.backend.controller.request.SignupRequestDto;
import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor

@CrossOrigin(origins = "http://localhost:3000")
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

    @GetMapping("members/kakao/callback")
    public String kakaoLogin(@RequestParam String code) throws JsonProcessingException {
        memberService.kakaoLogin(code);
        return "redirect:/kakao";
    }

}