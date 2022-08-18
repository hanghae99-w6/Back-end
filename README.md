![logo](https://user-images.githubusercontent.com/109033607/185214064-b9bb56c9-b2e1-4532-b0ec-3fd59c722075.png)
##
## 🙌 프로젝트 소개

영화, 드라마, 예능을 리뷰하는 사이트입니다.
컨텐츠를 보고 별점과 코멘트를 남겨주세요.


🗓 프로젝트 기간
2022년 08월 11일 ~ 2022년 8월 18일 (1주)

⚒️ 프로젝트 아키텍처

🛠 기술스택

<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white"> 
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white"> 
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> 

 <img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
 <img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
 <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">



😎 백엔드 팀원 소개

    이름	깃허브 주소

    이길종	

    이장원	
    
    한동훈 https://github.com/hdonghun

📚 와이어 프레임 / API 명세서
여기를 눌러주세요

✨ 핵심 기능

    (1)로그인, 회원가입

        : JWT를 이용하여 로그인과 회원가입을 구현하였습니다.

        : 아이디와 닉네임의 중복확인이 가능합니다.

        : 소셜 로그인 기능이 가능합니다.

    (2)CRUD

        : 각 카테고리별 영화/드라마/예능 사용자별 후기들을 참고 할 수 있습니다!

        : 댓글 기능을 통해, 다른 사용자와 경험을 공유 할 수 있습니다.

        : 좋아요 👍 및 별점을 통해 직관적인 후기 참조 가능 

🔥 트러블슈팅

    (1) 로그인 후 새로고침 시 로그아웃되는 문제 : 회원가입이 되어있는지 확인가능한 API 생성하여 FE 에서 API 요청하면 BE 에서 User 정보를 보내줌, 
        매 기능마다 FE 에서 header 에서 토큰을 포함해서 전달하면 유효한지 여부 상호 확인

        (2) json parse error : FE(Object) <-> BE(String) /  (BE) List 형태로 데이터 Request 요청 ( FE ) List 형태에서 바로 값만 꺼내서 사용함.

        (3) 배열 안의 배열 json 형태로 전달하기(게시글 작성 시 Todolist) : (FE) [] 제거함

        (4) POST ) imageUrl을 Return 값으로 드린 후, 다시 ${payload.imageUrl} 로 POST 를 했을 때 값이 안담기는 경우 :  
        이미지 저장 경로 Ubuntu 서버 -> S3 서버로 변경 후 API 통합하여 전체 데이터로 받음.

        (5) Web Configer, CORS filter를 사용하였는데 JWT filter와 충돌 : Web Configer 만 사용, CorsConfigurationSource @Bean 생성

        (6) 이미지, 게시글 내용 함께 POST 시 header 에 Content type 누락 : JSON, form 데이터 모두 넣기  "

📖 새로 적용해본 기술

개선해야할 사항

