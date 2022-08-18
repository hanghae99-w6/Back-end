package com.springw6.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springw6.backend.controller.request.*;
import com.springw6.backend.domain.Member;
import com.springw6.backend.domain.Message;
import com.springw6.backend.domain.UserDetailsImpl;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
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
   private final TokenProvider tokenProvider;

   //회원가입 함수
   @Transactional
   public ResponseEntity<?> signupMember(SignupRequestDto requestDto) {
      //현재 있는 닉네임인지 확인, 만약 널값이 아니면 이미 존재하는 닉네임이란 뜻으로, 아이디가 중복된다는 메시지 출력
      if (null != isPresentMember(requestDto.getNickname())) {
         return new ResponseEntity<>(Message.fail("DUPLICATED_NICKNAME", "아이디가 중복됩니다."), HttpStatus.ALREADY_REPORTED);
      }
      //중복되지 않으면 발더를 이용해 생성 후 저장
      Member member = Member.builder()
              .nickname(requestDto.getNickname())
              .password(passwordEncoder.encode(requestDto.getPassword()))
              .loginId(requestDto.getLoginId())
              .build();
      memberRepository.save(member);
      //리턴문
      return new ResponseEntity<>(Message.success("회원가입에 성공했습니다."), HttpStatus.OK);
   }

   //닉네임 더블체크 함수, 이미 존재하는 닉네임인지 아닌지 확인
   @Transactional
   public ResponseEntity<?> nicknameDubCheck(NicknameCheckRequestDto requestDto) {

      Member member = isPresentMember(requestDto.getNickname());
      //널값이 아니면 이미 존재하는 닉네임이라는 뜻
      if (null != member) {
         return new ResponseEntity<>(Message.fail("NICKNAME_ALREADY_USE", "사용 불가능한 닉네임입니다."), HttpStatus.ALREADY_REPORTED);
      }
      //널값이면 사용 가능하다는 메시지 출력
      return new ResponseEntity<>(Message.success("사용 가능한 닉네임입니다."), HttpStatus.OK);
   }

   //아이디 더블체크 함수
   @Transactional
   public ResponseEntity<?> loginIdDubCheck(LoginIdCheckRequestDto requestDto) {

      Member member = isPresentLoginId(requestDto.getLoginId());
      //널값이 아니면 이미 존재하는 아이디라는 뜻
      if (null != member) {
         return new ResponseEntity<>(Message.fail("EMAIL_ALREADY_USE", "사용 불가능한 이메일입니다."), HttpStatus.ALREADY_REPORTED);
      }
      //널값이면 사용 가능하다는 메시지 출력
      return new ResponseEntity<>(Message.success("사용 가능한 이메일입니다."), HttpStatus.OK);
   }

   //로그인 함수
   @Transactional
   public ResponseEntity<?> loginMembers(LoginRequestDto requestDto, HttpServletResponse response) {
      //로그인 리퀘스트에서 로그인 아이디를 가져옴
      Member member = isPresentLoginId(requestDto.getLoginId());
      //널값이면 아무것도 입력 안했다고 판단하여, 이메일을 입력하라는 메시지 출력
      if (null == member) {
         return new ResponseEntity<>(Message.fail("LOGINID_NOT_FOUND", "이메일을 입력하세요."), HttpStatus.UNAUTHORIZED);
      }
      //패스워드 일치 함수를 통해 검증 시, 일치하지 않으면 비밀번호를 일치시키라는 메시지 출력
      if (!member.validatePassword(passwordEncoder, requestDto.getPassword())) {
         return new ResponseEntity<>(Message.fail("INVALID_MEMBER", "비밀번호가 일치하지 않습니다."), HttpStatus.NOT_FOUND);
      }
      //전부통과하면 로그인 리퀘스트를 통해 들어온 데이터를 통해 토큰 생성
      TokenDto tokenDto = tokenProvider.generateTokenDto(member);
      //토큰 속 Authorization, Refresh-Token을 꺼내고 Access-Token-Expire-Time을 설정, 이를 해더로 전달
      tokenToHeaders(tokenDto, response);

      return new ResponseEntity<>(Message.success("로그인에 성공하였습니다."), HttpStatus.OK);
   }

   //닉네임 검증 함수
   @Transactional(readOnly = true)
   public Member isPresentMember(String loginId) {
      Optional<Member> optionalMember = memberRepository.findByNickname(loginId);
      return optionalMember.orElse(null);
   }

   //로그인 아이디 검증 함수
   @Transactional(readOnly = true)
   public Member isPresentLoginId(String nickname) {
      Optional<Member> optionalLoginId = memberRepository.findByLoginId(nickname);
      return optionalLoginId.orElse(null);
   }

   //토큰을 헤더로 전달시킬 함수, 토큰 속 Authorization, Refresh-Token을 꺼내고 Access-Token-Expire-Time을 설정함
   public void tokenToHeaders(TokenDto tokenDto, HttpServletResponse response) {
      response.addHeader("Authorization", "Bearer " + tokenDto.getAccessToken());
      response.addHeader("Refresh-Token", tokenDto.getRefreshToken());
      response.addHeader("Access-Token-Expire-Time", tokenDto.getAccessTokenExpiresIn().toString());
   }

   //카카오 로그인
   public TokenDto kakaoLogin(String code) throws JsonProcessingException {
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

      Member member = isPresentLoginId(kakaoUser.getLoginId());
      TokenDto tokenDto = tokenProvider.generateTokenDto(member);
      return tokenDto;

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
      body.add("redirect_uri", "http://localhost:3000/kakao/callback");
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
      ResponseEntity<String> response = rt.exchange(
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
              .get("email").asText();

      System.out.println("카카오 사용자 정보: " + id + ", " + nickname + ", " + loginId);
      return new KakaoUserInfoDto(id, nickname, loginId);
   }

}