![logo](https://user-images.githubusercontent.com/109033607/185214064-b9bb56c9-b2e1-4532-b0ec-3fd59c722075.png)
##
### 🙌 프로젝트 소개

영화, 드라마, 예능을 리뷰하는 사이트입니다.
컨텐츠를 보고 별점과 코멘트를 남겨주세요.


### 🗓 프로젝트 기간
2022년 08월 11일 ~ 2022년 8월 18일 (1주)

### ⚒️ 프로젝트 아키텍처


### 🛠 기술스택

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> <img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> 

 <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white"> <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">



### 😎 백엔드 팀원 소개
이름 | 깃허브 주소 |
---|---|
이길종	| https://github.com/Jongleee
이장원 |	https://github.com/wkddnjs
한동훈 | https://github.com/hdonghun

### 📚 와이어 프레임 / API 명세서

<details>
<summary>여기를 눌러주세요</summary>
<div markdown="1">

<br>
  
[figma로 열기](https://www.figma.com/file/dapEFyHroe0F7veKdijnio/Mini-Project?node-id=0%3A1)
<br>

[노션으로 열기](https://www.notion.so/5-abb0b2421aa6449abd1b7a4251a9e819)

</div>
</details>
<br>

### ✨ 핵심 기능

1. 로그인, 회원가입

        : JWT를 이용하여 로그인과 회원가입을 구현하였습니다.

        : 아이디와 닉네임의 중복확인이 가능합니다.

        : OAuth 2.0을 이용하여 카카오 소셜 로그인 기능이 가능합니다.

2. CRUD

        : 각 카테고리별 영화/드라마/예능 사용자별 후기들을 참고 할 수 있습니다!
        
        : 글 작성시 이미지를 업로드 할 수 있습니다.

        : 댓글 기능을 통해, 다른 사용자와 경험을 공유 할 수 있습니다.

        : 좋아요 👍 및 별점을 통해 직관적인 후기 참조 가능 

### 🔥 트러블슈팅
#### 1. 카카오 계정을 통한 로그인 api 구현 과정의 여러 문제들
<details>
<summary>해결방안</summary>
<div markdown="1">
 <br>
 
      - 카카오 로그인 같은 경우에는 실제 api 호출이 필요하여 서버를 열어두는 과정이 필요함
      - 이 과정에서 백엔드와 프론트 사이를 왔다갔다 하는 부분에서 문제가 발생함
      - 디버깅을 할 수 없기 때문에 우분투를 열어두고 오류를 찾아서 해결하는 방식을 사용함 
 
<br>  
 
    1.KOE320
    - 로그인 요청 여러번 되는 경우 

    2.KOE303
    - 인가 코드 요청 시 사용한 redirect_uri 와 액세스 토큰 요청 시 사용한 redirect_uri 가 다른 경우
    - 백엔드와 프론트에서 같은 uri를 사용해 주어야 함

    3.이미 로그인 처리가 된 경우
    - 로그인 과정에 인증 코드가 발급된 경우 로그인 한 것으로 간주하므로 계정 연결을 지워줄 필요성이 있음 
    -> 카카오 계정 관리 페이지 https://accounts.kakao.com/weblogin/account/partner

    4.카카오에서 제공하는 정보와 변수명
     <br>
     ![](https://velog.velcdn.com/images/jongleee/post/b69022cd-299e-492d-922f-70683d658bb9/image.png)
     <br>
    ```java
    String nickname = jsonNode.get("properties")
                  .get("nickname").asText();
          String loginId = jsonNode.get("kakao_account")
                  .get("email").asText();
    ```
    의 형태로 닉네임과 이메일을 받아올 수 있음
 <br>
 <br>
 <br>

</details>
 

#### 2. CORS문제 설정 다 했음에도 안됨
<details>
<summary>해결방안</summary>
<div markdown="1">
 <br>
CORS설정 내역
 <br>
 
 
 ```java
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
 http
          .cors().configurationSource(corsConfigurationSource());
          ...후략...
```

```java
 @Bean
    public CorsConfigurationSource corsConfigurationSource() {
       final CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://3.37.127.16:8080"));
        configuration.addAllowedHeader("*");
        configuration.addAllowedHeader("Authorization");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); //preflight 결과를 1시간동안 캐시에 저장
        configuration.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
```    
위와 같이 필터 부분에 cors설정을 해주고 마찬가지로 필터 부분에 아래의 Bean을 추가하였으나 cors에러가 해결되지 않았다. 

이유는 configure 파트에서 H2console 사용을 위해 추가해놓은 Bean 때문
사실 이유는 모르는데 주석처리하니까 됨 아마 충돌 문제일듯
```java
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
//        return (web) -> web.ignoring()
//                .antMatchers("/h2-console/**")
//                .antMatchers("/api/members/**")
//                .antMatchers("/favicon.ico");
//
//    }
```
 <br>
 <br>
 <br>

</details>
 
 
#### 3. 깃허브 충돌 문제
<details>
<summary>해결방안</summary>
<div markdown="1">
 <br>
최대한 충돌을 발생시키지 않으려고 여러 방법을 시도했는데 그냥 풀리퀘스트 하고 비교해서 처리하는게 가장 편했다!

 <br>
 <br>
</details>

#### 4. 양쪽 클래스에서 서로 참조하는 경우 순환오류 발생
<details>
<summary>해결방안</summary>
<div markdown="1">
 <br>
상호 참조 하는 경우를 만들지 말자
 <br>
 <br>

</details>

#### 5. 이미지 업로드시 기본 용량 제한이 1MB여서 문제가 발생함
<details>
<summary>해결방안</summary>
<div markdown="1"> 
 <br>
application.properties 파일에
 
 
```java
spring.servlet.multipart.maxFileSize=10MB
spring.servlet.multipart.maxRequestSize=10MB
 ```

 와 같이 제한을 설정할 수 있음
 <br>
 <br>
 <br>
</details>

#### 📖 새로 적용해본 기술
- OAuth 2.0을 통한 소셜 로그인
 
 
개선해야할 사항

