## WATCHAO 📺

평소에 궁금해 했던 최신 영화/드라마/예능
이제는 다른 사람들의 솔직한 후기, 공유하세요!


### 1. 제작 기간 & 팀원 소개
5인 4조 팀 프로젝트
이길종 : 
이장원 : 
한동훈 : 이미지 업로드, POST CRUD
신범수 : 
성필상 :

### 2. 사용 기술

Back-end :
  Python 3
  h2
  MySql
  AWS EC2
  JWT(발급 후 토큰 검증만 하면 되기 때문에 추가 저장소가 필요 없다. 가볍다. 필요한 모든 정보를 자체적으로 지니고 있기(self-contained)때문에 두 개체 사이에서 손쉽게 전달 될 수 있다.)

Front-end :

### 3. 와이어프레임 및 구현 영상


### 4. 핵심기능

(1)로그인, 회원가입
: JWT를 이용하여 로그인과 회원가입을 구현하였습니다.

: 아이디와 닉네임의 중복확인이 가능합니다.

: 소셜 로그인 기능이 가능합니다.

(2)CRUD
: 각 카테고리별 영화/드라마/예능 사용자별 후기들을 참고 할 수 있습니다!

: 댓글 기능을 통해, 다른 사용자와 경험을 공유 할 수 있습니다.

: 좋아요 👍 및 별점을 통해 직관적인 후기 참조 가능 


## 5. "Trouble Shooting : (우리 팀이 해결한 문제 정리)"

(1) 로그인 후 새로고침 시 로그아웃되는 문제 : 회원가입이 되어있는지 확인가능한 API 생성하여 FE 에서 API 요청하면 BE 에서 User 정보를 보내줌, 
매 기능마다 FE 에서 header 에서 토큰을 포함해서 전달하면 유효한지 여부 상호 확인
(2) json parse error : FE(Object) <-> BE(String) /  (BE) List 형태로 데이터 Request 요청 ( FE ) List 형태에서 바로 값만 꺼내서 사용함.
(3) 배열 안의 배열 json 형태로 전달하기(게시글 작성 시 Todolist) : (FE) [] 제거함
(4) POST ) imageUrl을 Return 값으로 드린 후, 다시 ${payload.imageUrl} 로 POST 를 했을 때 값이 안담기는 경우 :  
이미지 저장 경로 Ubuntu 서버 -> S3 서버로 변경 후 API 통합하여 전체 데이터로 받음.
(5) Web Configer, CORS filter를 사용하였는데 JWT filter와 충돌 : Web Configer 만 사용, CorsConfigurationSource @Bean 생성
(6) 이미지, 게시글 내용 함께 POST 시 header 에 Content type 누락 : JSON, form 데이터 모두 넣기  "
