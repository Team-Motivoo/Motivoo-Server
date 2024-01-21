# MOTIVOO 🏋🏻‍♀️💨

> 자녀와 부모를 잇는 매일 한 걸음, 가족과 함께 만들어 나가는 오늘의 운동 습관
>

<img src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/e87b6c2d-2bdf-4da7-9d02-8227f159a4f2">



## 🥕 서뿡이들 (MOTIVOO Server Developers)

|                                                                                                  박예준                                                                                                  |                                                 이혜연                                                  |                                      조찬우                                      |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------:|:----------------------------------------------------------------------------------------------------:|:-----------------------------------------------------------------------------:|
|                                                             <img src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/17bd3b2f-9ee5-4400-997d-d89a81a6f676" width="300"/>                                                             |            <img src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/d067272c-ab0b-44d7-8628-b11a7d76758b" width="300"/>            | <img src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/156e2625-786e-4b5b-8095-5131440a3c67" width="300"/> |
|                                                                                [jun02160](https://github.com/jun02160)                                                                                |                            [hyeyeonnnnn](https://github.com/hyeyeonnnnn)                             |                  [oownahcohc](https://github.com/oownahcohc)                  |
| Architecture 설계(Nginx, Docker)<br/>CI/CD 구축 및 관리<br/>ERD 및 DB 설계<br/>미션, 홈, 마이페이지 API 개발<br/>Firebase Realtime DB 연동<br/>Restdocs with Swagger UI 세팅</br>PreSigned Url(S3 Management)<br/>Slack 알림 연동 | ERD 및 DB 설계<br/>온보딩, 유저 API 개발<br/>인증/인가 구현(OAuthClient, Spring Security, Redis)<br/>JPA Soft Delete |            ERD 및 DB 설계<br/>JPA Query Inspector<br/>            |                                                                                                                                 |




## 🛠️ 개발 환경
| 통합 개발 환경                 | IntelliJ                                      |
|--------------------------|-----------------------------------------------|
| 배포                       | AWS EC2(Ubuntu)                               |
| Database                 | AWS RDS(MySQL), Firebase Realtime DB, Redis   |
| Spring Version           | 3.0.11                                        |
| Project Build Management | Gradle                                        |
| CI/CD Process            | Github Actions, Docker Compose                |
| ERD Diagram              | ERDCloud, MySQL Workbench                     |
| API Document             | Spring Restdocs(MockMvc), Swagger UI, Notion  |
| Java version             | Java 17                                       |
| Package Structure        | 도메인 패키지 구조                                    |
| File Upload              | AWS S3(PreSigned Url)                         |
| ETC                      | JPA, Redis, Spring Security, Spring Scheduler |
| Webhook                  | Slack                                         |

<br/><br/>


## 🔧 System Architecture
<img src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/c557f8ff-1d10-4b9c-82fa-057f85c52caf" />
<br/><br/>

## ☁️ ER Diagram
<img width=600 src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/fcc46c18-c238-4e00-84fc-2dc7c905470a">
<br/><br/>

## 📄 API Document
<img width="900" alt="image" src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/c8162be6-8855-4486-be6f-1bd1c4b7485e">


## 📂 Project Structure

<details>
<summary>자세히 보기</summary>
<div markdown="1">


```yaml
📂 Motivoo-Server

🗂 src
    🗂 main
        🗂 java/sopt/org/motivooServer
            📁 domain
                🗂 auth    // 인증, 인가 관련 로직
                    🗂 config
                    🗂 controller
                    🗂 dto
                      🗂 redis
                      🗂 request
                      🗂 response
                    🗂 repository
                🗂 common   // BaseTimeEntity (createdAt, updatedAt)
                🗂 health   // Health 
                    🗂 controller
                    🗂 dto
                      🗂 request
                      🗂 response
                    🗂 entity
                    🗂 exception
                    🗂 repository
                    🗂 service
                🗂 mission   // Mission, UserMission, UserMissionChoices, MissioQuest 테이블
                    🗂 controller
                    🗂 dto
                      🗂 request
                      🗂 response
                    🗂 entity
                    🗂 exception
                    🗂 repository
                    🗂 service
                🗂 parentchild  // Parentchild 부모-자녀 관계 테이블
                    🗂 controller
                    🗂 dto
                      🗂 request
                      🗂 response
                    🗂 entity
                    🗂 exception
                    🗂 repository
                    🗂 service
                🗂 user       // User 
                    🗂 controller
                    🗂 dto
                      🗂 request
                      🗂 response
                    🗂 entity
                    🗂 exception
                    🗂 repository
                    🗂 service
            📁 global 
                🗂 advice     // 예외 핸들러
                🗂 config
                    🗂 swagger
                🗂 external    // 외부 연동
                    🗂 firebase
                      🗂 config
                    🗂 s3
                      🗂 config
                    🗂 slack
                🗂 healthcheck  // 헬스체크 API
                🗂 response     // 공통 응답 클래스 관리
        🗂 resources
            application.yaml
            application-local.yaml
            application-dev.yaml
            🗂 static
              🗂 docs

    🗂 test 
        🗂 java/sopt/org/motivooServer
          🗂 controller   // API 문서화를 위한 컨트롤러 테스트
          🗂 fixture      // 테스트용 Entity, DTO 객체 Fixture 관리
          🗂 util
```



</div>
</details>

<br/><br/>

## 📓 Backend Convention

### 👣 Git Flow

<img src="https://github.com/Team-Motivoo/Motivoo-Server/assets/80024278/2619fc0c-1b14-4abe-9407-43219fca9e71">

<details>
<summary>자세히 보기</summary>
<div markdown="1">

> **🔗 master, release, develop, feature, hotfix**

`master` : 최최최최최최종본 - stable all the time

`release` : 이번 릴리즈를 위한 브랜치

`develop` : 우리가 개발하면서 코드를 모을 공간, 배포하기 전까지는 이게 default로 하여 PR은 여기로 날립니다. (for 다음 릴리즈)

`feat` : 기능을 개발하면서 각자가 사용할 브랜치 (이슈 단위)
- Git flow 전략에 따라 → “**feat/#이슈번호-구현하려는기능**” 형식으로

    ex. feat/#3-social_login, feat/#8-slack_api

`fix` : 오류사항, 버그 해결 및 로직 일부 수정 시 사용할 브랜치 (이슈 단위)

`refactor` : 기능의 변경 없이 구조 개선 및 코드 리팩토링 시 사용할 브랜치 (이슈 단위)

`test` : 개인 연습 브랜치

`study` : 공부용 브랜치    ex. study/yejun

</div>
</details>

### 👣️ Code Review

<details>
<summary>자세히 보기</summary>
<div markdown="1">
    
#### Convention
> P1: 꼭 반영해주세요 (Request changes)<br/>
> P2: 적극적으로 고려해주세요 (Request changes)<br/>
> P3: 웬만하면 반영해 주세요 (Comment)<br/>
> P4: 반영해도 좋고 넘어가도 좋습니다 (Approve)<br/>
> P5: 그냥 사소한 의견입니다 (Approve)
>

#### Rule
- 서로 상대 실수 한 것 없는지 귀찮아도 꼭 읽어보기
- 긍정적인 코멘트 적극적으로 남겨주기
- 우선순위 반영한 코드리뷰 진행하기 ex.`[P1] 이건 꼭 반영해주셔야해요!`

</div>
</details>

### 👣 Commit Convention

<details>
<summary>자세히 보기</summary>
<div markdown="1">

```
# <타입>: <제목> #이슈번호 형식으로 작성하며 제목은 최대 50글자 정도로만 입력
# 제목을 아랫줄에 작성, 제목 끝에 마침표 금지, 무엇을 했는지 명확하게 작성

################
# 본문(추가 설명)을 아랫줄에 작성

################
# 꼬릿말(footer)을 아랫줄에 작성 (관련된 이슈 번호 등 추가)

################
# 아이콘	코드	     설명
# 🎨 style:      코드의 구조/형태 개선
# 🔥 remove:      코드/파일 삭제
# 🐛 bugfix:      버그 수정
# 🚑 hotfix:      긴급 수정
# ✨ feat:      새로운 기능 구현
# 📝 docs:      문서 추가/수정
# 🎉 init:      프로젝트 시작
# ✅ test:      테스트 추가/수정
# 🔖 release:      릴리즈/버전 태그
# 🔧 chore:      동작에 영향 없는 코드 or 변경 없는 변경사항(주석 추가 등) or 디렉토리 구조 변경
# ♻️  refactor:      코드 리팩토링, 전면 수정
# ⚡️ fix:      간단한 수정
################
```

- 커밋 예시 <br/>
  🎉 init: 프로젝트 시작  → git commit -m "🎉 init: 프로젝트 시작 #1"


- 커밋 단위
  - 세부 기능 기준
  - 기능 우선 순위 정리 파일 참고
  - #이슈번호 붙이는 단위 : **FEAT, FIX, REFACTOR**

    ex. `git commit -m “[FEAT] 로그인 기능 구현 #2”`

</div>
</details>


### 👣 Code Convention

<details>
<summary>자세히 보기</summary>
<div markdown="1">

1. 기본적으로 네이밍은 **누구나 알 수 있는 쉬운 단어**를 선택한다.
1. 변수는 CamelCase를 기본으로 한다.
1. URL, 파일명 등은 kebab-case를 사용한다.
1. 패키지명은 단어가 달라지더라도 무조건 소문자를 사용한다.
1. ENUM이나 상수는 대문자로 네이밍한다.
1. 함수명은 소문자로 시작하고 **동사**로 네이밍한다.
1. 클래스명은 **명사**로 작성하고 UpperCamelCase를 사용한다.
1. 객체 이름을 함수 이름에 중복해서 넣지 않는다. (= 상위 이름을 하위 이름에 중복시키지 않는다.)
1. 컬렉션은 복수형을 사용한다. ex. userMissions (O) userMissionList (X)
1. 이중적인 의미를 가지는 단어는 지양한다.
1. 의도가 드러난다면 되도록 짧은 이름을 선택한다.
1. 함수의 부수효과를 설명한다.
1. LocalDateTime -> xxxAt, LocalDate -> xxxDt로 네이밍
1. 객체를 조회하는 함수는 JPA Repository에서 findXxx 형식의 네이밍 쿼리메소드를 사용하므로 개발자가 작성하는 Service단에서는 되도록이면 getXxx를 사용하자.

</div>
</details>
