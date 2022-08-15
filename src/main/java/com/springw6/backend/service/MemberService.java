package com.springw6.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springw6.backend.controller.request.*;
import com.springw6.backend.controller.response.ResponseDto;
import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.UserDetailsImpl;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> signupMember(SignupRequestDto requestDto) {
        if (null != isPresentMember(requestDto.getNickname())) {
            return ResponseDto.fail("DUPLICATED_NICKNAME",
                    "아이디가 중복됩니다.");
        }

        Member member = Member.builder()
                .nickname(requestDto.getNickname())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .loginId(requestDto.getLoginId())
                .build();
        memberRepository.save(member);
        return ResponseDto.success("회원가입에 성공하였습니다.");
    }


    @Transactional
    public ResponseDto<?>nicknameDubCheck(NicknameCheckRequestDto requestDto) {

        Member member = isPresentMember(requestDto.getNickname());

        if (null != member) {
            return ResponseDto.fail("NICKNAME_ALREADY_USE", "사용 불가능한 닉네임입니다.");
        }
        return ResponseDto.success("사용 가능한 닉네임입니다.");
    }


    @Transactional
    public ResponseDto<?> loginIdDubCheck(LoginIdCheckRequestDto requestDto) {

        Member member = isPresentLoginId(requestDto.getLoginId());

        if (null != member) {
            return ResponseDto.fail("MEMBER_ALREADY_USE", "사용 불가능한 이메일입니다.");
        }

        return ResponseDto.success("사용 가능한 이메일입니다.");

    }


    @Transactional
    public ResponseDto<?> loginMembers(LoginRequestDto requestDto, HttpServletResponse response) {
        Member member = isPresentLoginId(requestDto.getLoginId());
        if (null == member) {
            return ResponseDto.fail("LoginId_NOT_FOUND",
                    "이메일을 입력하세요.");
        }

        if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
            return ResponseDto.fail("INVALID_MEMBER", "비밀번호가 일치하지 않습니다.");
        }

        TokenDto tokenDto = tokenProvider.generateTokenDto(member);
        tokenToHeaders(tokenDto, response);

        return ResponseDto.success("로그인이 성공하였습니다.");
    }


    public ResponseDto<?> logoutMembers(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return ResponseDto.fail("INVALID_TOKEN", "refresh token is invalid");
        }
        Member member = tokenProvider.getMemberFromAuthentication();
        if (null == member) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "member not found");
        }
        return tokenProvider.deleteRefreshToken(member);
    }


    @Transactional(readOnly = true)
    public Member isPresentMember(String loginId) {
        Optional<Member> optionalMember = memberRepository.findByNickname(loginId);
        return optionalMember.orElse(null);
    }


    @Transactional(readOnly = true)
    public Member isPresentLoginId(String nickname) {
        Optional<Member> optionalLoginId = memberRepository.findByLoginId(nickname);
        return optionalLoginId.orElse(null);
    }


    public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
        response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
        response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
        response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
    }


    public void kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);
        // 2. 토큰으로 카카오 API 호출
        KakaoUserInfoDto kakaoUserInfo = getKakaoUserInfo(accessToken);

        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoUserInfo.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId)
                .orElse(null);

        if (kakaoUser == null) {
            // 회원가입
            // username: kakao nickname
            String nickname = kakaoUserInfo.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            // email: kakao email
            String loginId = kakaoUserInfo.getLoginId();
            // role: 일반 사용자

            kakaoUser = new Member(nickname, encodedPassword, loginId, kakaoId);
            memberRepository.save(kakaoUser);
        }

        // 4. 강제 kakao로그인 처리
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

    }
    @Value("${myKaKaoRestAplKey}")
    private String myKaKaoRestAplKey;

    private String getAccessToken(String code) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", myKaKaoRestAplKey);
        body.add("redirect_uri", "http://localhost:8080/members/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }


    private KakaoUserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String>response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties")
                .get("nickname").asText();
        String loginId = jsonNode.get("kakao_account")
                .get("loginId").asText();

        System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + loginId);
        return new KakaoUserInfoDto(id, nickname, loginId);
    }

}