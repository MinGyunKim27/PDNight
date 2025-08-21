# 🌃 판교의 밤 (PDNight)
개발자들을 위한 오프라인 번개모임 플랫폼

## 📑 목차
- [프로젝트 소개](#-프로젝트-소개)
- [팀원 소개](#-팀원-소개)
- [기술 스택](#-기술-스택)
- [아키텍쳐](#-아키텍쳐)
- [서비스 흐름](#-서비스-흐름)
- [설계 문서](#-설계-문서)
  - [ERD](#-erd)
  - [와이어 프레임](#-와이어-프레임)
  - [ERD](#-erd)
  - [API 명세서](#-api-명세서)
- [핵심 기능](#-핵심-기능)
- [기술적 의사 결정](#-기술적-의사-결정)
- [트러블 슈팅](#-트러블-슈팅)
- [성능 개선](#-성능-개선)

---
## 🌃 프로젝트 소개
>`판교의 밤`은 판교를 중심으로 한 실시간 번개 모임 플랫폼 입니다. <br/>
> 개발이라는 공통 관심사를 매개로 가볍고 빠른 만남을 주선하고, 사용자 리뷰, 모임 기록, 채팅을 통해 지속적인 연결과 신뢰를 만들어갑니다. <br/>
> 이 플랫폼은 근무지역/거주지역, 직군, 취미/기술스택 등의 공통 관심사를 기반으로 한 모임을 추천하고 연결합니다.

---

### 🙅 기존 오프라인 모임의 문제점

<aside>


<div style="flex: 1; border: 1px solid #ddd; border-radius: 8px; padding: 10px;">
- 모임 정보에 대한 분산 <br/>
- 신원 불명, 후기 시스템 부재 <br/>
- 모임 이후 지속적 관계 유지의 어려움
</div>

</aside>


### 🙆 판교의 밤이 해결하고자 하는 방향성

| 관심사 기반의 추천                                    | 사용자 신뢰도 관리              | 지속적인 연결 제공               |
| --------------------------------------------- | ----------------------- | ------------------------ |
| - 취미 / 기술 스택 <br/> - 모임 별 태그 <br/> - 조건 검색 기능 | - 사용자 리뷰 <br/> - 프로필 기능 | - 채팅 시스템 <br/> - 사용자 팔로우 |


---

## 🚀 개발 타임라인

2025-07-18 ~ 2025-08-19

- **7/18 ~ 7/23**: MVP 개발 및 배포 · 기본 기능 구현 · CI/CD 설정(GitHub Actions + Docker) · AWS 배포 - 브랜치 version/V1

- **7/24 ~ 7/28**: 기능 및 성능 개선 · 추가 기능 구현 · Redis Cache 적용 · 동시성 제어 - 브랜치 version/V2

- **7/29 ~ 8/04**: 도메인 주도 설계 · DDD 아키텍처 도입 · 패키지 및 도메인 계층 재구성 - 브랜치 version/V3

- **8/05 ~ 8/12**: 이벤트 구조 확장 및 시각화 · Kafka 이벤트 발행 · 로깅/모니터링 체계 구축 - 브랜치 version/V4

- **8/13 ~ 8/19**: 기능 고도화 · Elasticsearch 성능 개선 · OAuth 적용 · MongoDB 연동 - 브랜치 develop

>`개발 기간` : 25.07.16 ~ 08.25

---
## 👨‍👩‍👧‍👦 팀원 소개

<div align="center">
<table>
  <tr>
    <td>
      <div align="center" style="width:250px; height:300px;">
        <img src="https://avatars.githubusercontent.com/u/89957296?v=4" width="120px;" alt=""/>
        <br /><b>김민균 [팀장]</b>
        <br />Kafka 모니터링
        <br />프로젝트 총괄
        <br />Deploy
        <br />
        <a href="https://github.com/MinGyunKim27" target="_blank">GitHub</a> | 
        <a href="https://velog.io/notifications" target="_blank">Blog</a>
      </div>
    </td>
    <td>
      <div align="center" style="width:250px; height:300px;">
        <img src="https://avatars.githubusercontent.com/u/79428775?v=4" width="120px;" alt=""/>
        <br /><b>김지현 [부팀장]</b>
        <br />Logging
        <br />동시성 제어
        <br />엘라스틱 Bulk 처리
        <br />
        <a href="https://github.com/jh-01" target="_blank">GitHub</a> | 
        <a href="https://everyday-for-coding.tistory.com/" target="_blank">Blog</a>
      </div>
    </td>
    <td>
      <div align="center" style="width:250px; height:300px;">
        <img src="https://avatars.githubusercontent.com/u/115523810?v=4" width="120px;" alt=""/>
        <br /><b>류수연 [팀원]</b>
        <br />Logging
        <br />Redis Cache
        <br />쿼리 최적화
        <br />
        <a href="https://github.com/ryusu12" target="_blank">GitHub</a> | 
        <a href="https://velog.io/@rsy7567/posts" target="_blank">Blog</a>
      </div>
    </td>
  </tr>
  <tr>
    <td>
      <div align="center" style="width:250px; height:300px;">
        <img src="https://avatars.githubusercontent.com/u/114732394?v=4" width="120px;" alt=""/>
        <br /><b>박진 [팀원]</b>
        <br />Kafka 재시도 설계
        <br />OAuth
        <br />Refresh Token
        <br />
        <a href="https://github.com/ParkJin0814" target="_blank">GitHub</a> | 
        <a href="https://velog.io/@klkl97/posts" target="_blank">Blog</a>
      </div>
    </td>
    <td>
      <div align="center" style="width:250px; height:300px;">
        <img src="https://avatars.githubusercontent.com/u/64243394?v=4" width="120px;" alt=""/>
        <br /><b>이안근 [팀원]</b>
        <br />Kafka 재시도 설계
        <br />Elasticsearch
        <br />MongoDB
        <br />
        <a href="https://github.com/roog23" target="_blank">GitHub</a> | 
        <a href="https://roog23.tistory.com/" target="_blank">Blog</a>
      </div>
    </td>
    <td>
      <div align="center" style="width:250px; height:300px;">
        <img src="https://avatars.githubusercontent.com/u/180647905?v=4" width="120px;" alt=""/>
        <br /><b>최영재 [팀원]</b>
        <br />Kafka 재시도 설계
        <br />Elasticsearch
        <br />문서 정리
        <br />
        <a href="https://github.com/teopteop" target="_blank">GitHub</a> | 
        <a href="https://velog.io/@teopteop/posts" target="_blank">Blog</a>
      </div>
    </td>
  </tr>
</table>
</div>

---
## 🛠️ 기술 스택

![기술스택.png](docs/image/%EA%B8%B0%EC%88%A0%EC%8A%A4%ED%83%9D.png)

---
## 🏛️ 아키텍쳐

![아키텍쳐.png](docs/image/%EC%95%84%ED%82%A4%ED%85%8D%EC%B3%90.png)

---
## ➡️ 서비스 흐름

![서비스_흐름.png](docs/image/%EC%84%9C%EB%B9%84%EC%8A%A4_%ED%9D%90%EB%A6%84.png)

---
## 📘 설계 문서

### 🔗 ERD
![에그리거트_관계도.png](docs/image/%EC%97%90%EA%B7%B8%EB%A6%AC%EA%B1%B0%ED%8A%B8_%EA%B4%80%EA%B3%84%EB%8F%84.png)
![ERD_2.png](docs/image/ERD_2.png)

---
### 🖼️ 와이어 프레임
1. 회원가입/로그인
![로그인_와이어프레임.png](docs/image/%EB%A1%9C%EA%B7%B8%EC%9D%B8_%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84.png)

---
2. 게시글
![게시글_와이어프레임.png](docs/image/%EA%B2%8C%EC%8B%9C%EA%B8%80_%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84.png)

---
3. 메뉴
![메뉴_와이어프레임.png](docs/image/%EB%A9%94%EB%89%B4_%EC%99%80%EC%9D%B4%EC%96%B4%ED%94%84%EB%A0%88%EC%9E%84.png)

---
## 🌐 API 명세서

[API 명세서 By springdoc](https://pdnight.duckdns.org/swagger-ui)

<details>
  <summary> 인증 - Auth API </summary>

### 인증 - Auth API

| 기능      | Method   | Endpoint           | 설명                         | 권한     |
|---------|----------|--------------------|----------------------------|--------|
| 회원가입    | `POST`   | /api/auth/signup   | 서비스 이용을 위한 계정 생성           | PUBLIC |
| 로그인     | `POST`   | /api/auth/login    | 사용자 인증을 통해 액세스 토큰을 발급      | PUBLIC |
| 로그아웃    | `POST`   | /api/auth/logout   | 로그아웃                       | USER   |
| 비밀번호 수정 | `PATCH`  | /api/auth/password | 비밀번호 수정                    | USER   |
| 회원탈퇴    | `DELETE` | /api/auth/withdraw | 사용자의 계정을 비활성화하고 서비스 이용을 종료 | USER   |
| 토큰재발행   | `POST`   | /api/auth/reissue  | 리프레시 토큰을 이용하여 토큰 재발급       | USER   |

</details>

---
<details>
  <summary> OAuth API </summary>

### OAuth API

| 기능        | Method | Endpoint               | 설명        | 권한     |
|-----------|--------|------------------------|-----------|--------|
| 구글 로그인    | `GET`  | /oauth/login/google    | 구글 로그인    | PUBLIC |
| 구글 로그인 콜백 | `GET`  | /oauth/callback/google | 구글 로그인 콜백 | PUBLIC |

</details>

---
<details>
  <summary> 사용자 - User API </summary>

### 사용자 - User API

| 기능             | Method   | Endpoint                        | 설명                   | 권한   |
|----------------|----------|---------------------------------|----------------------|------|
| 내 쿠폰 사용        | `POST`   | /api/users/my/user-coupons/{id} | 내 쿠폰 사용              | USER |
| 팔로우 추가         | `POST`   | /api/users/{userId}/follow      | 팔로우 추가               | USER |
| 프로필 수정         | `PATCH`  | /api/users/my/profile           | 프로필 수정               | USER |
| 언팔로우           | `DELETE` | /api/users/{userId}/follow      | 언팔로우                 | USER |
| 내 프로필 조회       | `GET`    | /api/users/my/profile           | 내 프로필 조회             | USER |
| 상대 프로필 조회      | `GET`    | /api/users/{Id}/profile         | 상대 프로필 조회            | USER |
| 인물 검색 조회 기능 추가 | `GET`    | /api/users/search               | 인물 검색 조회 기능 추가       | USER |
| 내 받은 리뷰 조회     | `GET`    | /api/users/my/review            | 내 받은 리뷰 조회           | USER |
| 팔로잉 목록 조회      | `GET`    | /api/users/my/following         | 팔로잉 목록 조회            | USER |
| 내 쿠폰 조회        | `GET`    | /api/users/my/user-coupons      | 해당 쿠폰의 상세 정보를 반환합니다. | USER |
| 사용자 평가 조회      | `GET`    | /api/users/{id}/evaluation      | 사용자 평가 조회            | USER |

</details>

---
<details>
  <summary> 취미/기술스택  - Hobby,TechStack API </summary>

### 취미/기술스택  - Hobby,TechStack API

| 기능           | 메소드    | 엔드포인트           | 권한   | 설명             |
|--------------|--------|-----------------|------|----------------|
| 취미 추가        | `POST` | /api/hobbies    | User | 나의 취미 항목 추가    |
| 취미 리스트 조회    | `GET`  | /api/hobbies    | User | 전체 취미 항목 목록 조회 |
| 기술 스택 추가     | `POST` | /api/techStacks | User | 나의 기술 스택 추가    |
| 기술 스택 리스트 조회 | `GET`  | /api/techStacks | User | 전체 기술 스택 목록 조회 |

</details>

---
<details>
  <summary> 태그  - Tag API </summary>

### 태그  - Tag API

| 기능        | 메소드    | 엔드포인트     | 권한   | 설명             |
|-----------|--------|-----------|------|----------------|
| 취미 추가     | `POST` | /api/tags | User | 태그 항목 추가       |
| 태그 리스트 조회 | `GET`  | /api/tags | User | 전체 태그 항목 목록 조회 |

</details>

---
<details>
  <summary> 게시글 - Post API </summary>

### 게시글 - Post API

| 기능                   | 메소드      | 엔드포인트                                            | 권한   | 설명                        |
|----------------------|----------|--------------------------------------------------|------|---------------------------|
| 게시글 참여 신청            | `POST`   | /api/posts/{postId}/participate                  | USER | 해당 게시글에 참여 신청             |
| 게시글 참여 취소            | `DELETE` | /api/posts/{postId}/participate                  | USER | 내가 신청한 참여를 취소             |
| 신청자 수락/거절            | `PATCH`  | /api/posts/{postId}/participate/users/{userId}   | USER | 신청자의 참여를 수락 또는 거절         |
| 신청자 목록 조회            | `GET`    | /api/posts/{postId}/participate                  | USER | 해당 게시글의 신청자 목록 조회         |
| 참여자 목록 조회            | `GET`    | /api/posts/{postId}/participate/confirmed        | USER | 확정된 참여자 목록 조회             |
| 게시글 목록 조회 (신청자 수 포함) | `GET`    | /api/posts                                       | USER | 신청자 수, 확정 참여자 수 포함 게시글 조회 |
| 게시글 참여 신청 (제한 로직 추가) | `POST`   | /api/posts/{postId}/participate                  | USER | 제한 조건이 적용된 게시글 참여 신청      |
| 게시글 초대               | `POST`   | /api/posts/{postId}/users/{userId}/invite        | USER | 특정 유저를 게시글에 초대            |
| 추천 게시글 목록 조회         | `GET`    | /api/posts/suggestedPosts                        | USER | 사용자에게 추천되는 게시글 목록 조회      |
| 초대 취소                | `DELETE` | /api/posts/{postId}/users/{userId}/invite        | USER | 게시글 초대를 취소함               |
| 게시글 좋아요              | `POST`   | /api/posts/{id}/likes                            | USER | 게시글에 좋아요를 누름              |
| 게시글 좋아요 취소           | `DELETE` | /api/posts/{id}/likes                            | USER | 게시글의 좋아요를 취소함             |
| 사용자 리뷰               | `POST`   | /api/posts/{postId}/participants/{userId}/review | USER | 참여한 사용자에게 리뷰 작성           |

</details>

---
<details>
  <summary> 댓글 - Comment API </summary>

### 댓글 - Comment API

| 기능       | Method   | Endpoint                                   | 설명                | 권한   |
|----------|----------|--------------------------------------------|-------------------|------|
| 댓글 추가    | `POST`   | /api/posts/{postId}/comments               | 댓글 추가             | USER |
| 대댓글 생성   | `POST`   | /api/posts/{postId}/comments/{id}/comments | 대댓글 생성            | USER |
| 댓글 수정    | `PATCH`  | /api/posts/{postId}/comments/{id}          | 댓글 수정             | USER |
| 댓글 삭제    | `DELETE` | /api/posts/{postId}/comments/{id}          | 특정 게시글에 달린 댓글을 삭제 | USER |
| 댓글 다건 조회 | `GET`    | /api/posts/{postId}/comments               | 댓글 다건 조회          | USER |

</details>

---
<details>
  <summary> 관리자 - ADMIN API </summary>

### 관리자 - ADMIN API

| 기능                | Method   | Endpoint                                | 설명                       | 권한    |
|-------------------|----------|-----------------------------------------|--------------------------|-------|
| [관리자] 사용자에게 쿠폰 할당 | `POST`   | /api/admin/users/coupons                | 사용자에게 쿠폰 할당              | ADMIN |
| [관리자] 닉네임 강제 변경   | `PATCH`  | /api/admin/users/{id}                   | 닉네임 강제 변경                | ADMIN |
| [관리자] 강제 회원 탈퇴    | `DELETE` | /api/admin/users/{id}                   | 강제 회원 탈퇴                 | ADMIN |
| [관리자] 전체 유저 조회    | `GET`    | /api/admin/users                        | 전체 유저 조회                 | ADMIN |
| [관리자] 게시글 삭제      | `DELETE` | /api/admin/posts/{id}                   | 게시글 삭제                   | ADMIN |
| [관리자] 프로모션 추가     | `POST`   | /api/admin/promotions                   | 프로모션 추가                  | ADMIN |
| [관리자] 프로모션 수정     | `PATCH`  | /api/admin/promotions/{id}              | 프로모션 수정                  | ADMIN |
| [관리자] 프로모션 삭제     | `DELETE` | /api/admin/promotions/{id}              | 프로모션 삭제                  | ADMIN |
| [관리자] 프로모션 참가자 조회 | `GET`    | /api/admin/promotions/{id}/participants | 사용자가 특정 프로모션에 참가 신청을 합니다 | ADMIN |
| [관리자] 쿠폰 생성       | `POST`   | /api/admin/coupons                      | 관리자가 새로운 쿠폰을 등록          | ADMIN |
| [관리자] 쿠폰 수정       | `PATCH`  | /api/admin/coupons/{id}                 | 쿠폰 수정                    | ADMIN |
| [관리자] 쿠폰 삭제       | `DELETE` | /api/admin/coupons/{id}                 | 쿠폰 삭제                    | ADMIN |
| [관리자] 쿠폰 조회       | `GET`    | /api/admin/coupons/{id}                 | 해당 쿠폰의 상세 정보를 반환         | ADMIN |
| [관리자] 댓글 삭제       | `DELETE` | /api/admin/posts/{postId}/comments/{id} | 게시글에 달린 댓글을 강제 삭제        | ADMIN |

</details>

---
## 💻 핵심 기능

>### 👤 User - 사용자
>- **추천 모임 제공**
>  - 위치(근무지/거주지), 연령대, 직군/취미/기술스택 기반으로 모임 추천
>  - **프로필 열람**
>    - 타 유저의 프로필을 확인하여 신뢰도 및 공통 관심사 파악
>  - **신뢰도 평가**
>    - 모임 종료 후 참여자 간 별점 및 코멘트 평가 기능
>  - **팔로우 기능**
>    - 팔로우한 유저를 모임 생성 시 초대 가능

> ### 🗓️ Post - 모임
>- **모임 필터 검색**
>  - 연령대, 직군, 취미/기술 등 관심사 기반 조건으로 필터링 검색 가능
>- **좋아요 기능**
>  - 모임을 찜해두고 추적 가능
>  - 좋아요 수가 많은 모임은 상단에 우선 노출
>- **댓글 기능**
>  - 모임에 대한 질문 및 커뮤니케이션 지원
>  - 대댓글 기능으로 추가 코멘트 가능 (단, 1단계만 허용)
>- **참여 신청 기능**
>  - 신청자 수 제한 기반 신청 및 수락/거절 기능
>  - 선착순 모임은 신청 순서대로 자동 승인
>  - Redisson Lock 기반 동시성 제어 적용
>- **모임 상태 관리**
>  - 모집 완료 시 `성사`, 종료된 모임은 `닫힘` 상태로 전환
>  - `닫힘` 상태 모임은 기록용으로 조회 가능
>- **초대 기능**
>  - 호스트는 모임 생성 후 특정 유저를 모임에 초대 가능
>  - 초대를 받은 사용자가 수락 시 자동으로 모임에 신청 → 수락 상태가 됨
>- **모임 후기 기능**
>  - 모임이 끝난 후 자유롭게 작성가능한 후기 기능 제공
>  - 이미지나 동영상 등 다양한 형식의 파일 첨부 가능

> ### 💬 Chat - 채팅
> - **모임 채팅방 제공**
>    - 모임 성사 시 자동 채팅방 생성
>    - 참여자 간 실시간 커뮤니케이션 가능
>    - 모임 종료 후에도 채팅방 유지
> - **1:1 또는 그룹 채팅 기능**
>    - 사용자가 독립적으로 채팅방 생성 가능
>    - 참여자 설정을 통해 접근 제어 가능

>### 🎉 Promotion - 프로모션
>- **프로모션 기능**
>  - 관리자가 특정 조건의 프로모션 생성 가능 (예: 유명인 초청 번개)

>### 🔔 Notification - 알림
>- **사용자에게 실시간으로 전송되는 알림 기능**
>  - 사용자의 활동에 따라 알림 생성
>    - 모임 신청 / 수락 / 거절 / 성사
>    - 모임 초대 / 수락 / 거절
>    - 팔로우한 사용자의 게시물 생성
>    - 사용자 리뷰 작성
>    - 쿠폰 지급 / 만료 임박
>    - 댓글 작성


---
## 🙋 기술적 의사 결정

<details>

<summary> GitHub Actions 도입 배경 및 선택 이유 </summary>

### [ GitHub Actions ]  
> 본 프로젝트의 CI/CD 자동화를 위한 도구로 사용 예정

---

### 1. 기술 설명

GitHub Actions는 GitHub 에서 제공하는 워크플로우 기반의 CI/CD 도구입니다.  
저장소 내 `.github/workflows` 디렉토리에 `.yml` 설정 파일을 추가하여  
코드 푸시, PR, 병합 등의 이벤트에 따라 자동으로 빌드, 테스트, 배포를 실행할 수 있습니다.

우리 팀은 AWS CodePipeline과 비교 검토 후 개발 편의성과 운영 효율성을 고려하여 GitHub Actions를 최종 도입하였습니다.

---

### 2. 기술 장점

1. 학습 및 사용 편의성
   - GitHub 저장소 내에서 바로 CI/CD 설정 가능 → 진입 장벽 낮음
   - 템플릿, 공식 문서, 커뮤니티 자료가 많아 학습이 쉬움
   - 설정 파일인 `.yml` 문법이 간단하고 유지보수가 용이
   - CodePipeline은 IAM, 리소스 연결 등 설정 절차가 복잡함

---

2. 비용 측면에서의 이점
   - Public 저장소는 완전 무료
   - Private 저장소는 월 2,000분 무료 제공
   - CodePipeline은 파이프라인당 과금 + 분당 과금 발생
   - 실제 비용 이슈가 발생한 사례도 존재

---

3. 속도 및 성능
   - Gradle 캐시 활용 가능 → 빌드 속도 향상
   - 병렬 처리 가능 → 빠른 피드백 루프 구성
   - 실 사용 사례에서도 CodePipeline보다 빠르다는 평가 다수

---

4. 확장성과 유연성
   - 다양한 서드파티 액션과 통합 가능
   - AWS 외에도 GCP, Azure 등 클라우드 연동 가능
   - Kubernetes 등 컨테이너 환경 확장 시에도 유리

---

5. 관리 및 통합 편의성
   - GitHub 워크플로우 내에서 코드부터 배포까지 한 번에 관리 가능
   - PR, 테스트, 배포 상태를 GitHub 에서 직접 추적 가능
   - 에러 발생 시 로그 확인이 간편함

---

### 3. 단점 / 주의사항

- 빌드 속도가 느릴 수 있음 (무료 러너 기준)
  - 퍼포먼스가 낮고 대기 시간 발생 가능

- 복잡한 워크플로우 관리 어려움
  - `.yml` 구조가 커질수록 가독성과 디버깅이 어려움

- 보안/네트워크 제약
  - 사설 네트워크 접근, SSH 설정 등에 별도 구성 필요

---

### 4. 도입 배경과 선택 이유

- 개발 진입 장벽이 낮아 빠르게 적용 가능
- 공식 문서와 오픈소스 사례가 풍부하여 다양한 설계 가능
- 과금 구조가 명확해 비용 예측이 쉬움
- 설정 및 유지보수가 간단하고 접근성도 뛰어남


</details>

<details>


<summary> Nginx 도입 배경 및 선택 이유 </summary>

### [ Nginx ]


### 1. 기술 설명

Nginx는 클라이언트의 요청을 받아 백엔드 서버로 전달해주는 리버스 프록시 서버이자  
HTML, JS, 이미지 등 정적 파일을 빠르게 처리할 수 있는 고성능 웹 서버입니다.

---

### 2. 기술 장점

1. 리버스 프록시 역할
    - 클라이언트 요청(HTTPS)을 받아 내부 애플리케이션 서버(HTTP)로 전달
    - 예: https://도메인/api/... → Nginx(443) → http://localhost:8080
    - Spring 서버를 직접 외부에 노출하지 않음 → 보안 계층 추가 가능

---

2. HTTPS / SSL 처리
    - SSL 인증서를 Nginx에 적용하여 HTTPS 통신 처리
    - Spring Boot 단독 사용 시보다 설정이 간단함
    - Let’s Encrypt + certbot 사용 시 무료 SSL 자동 갱신 가능

---

3. 로드 밸런싱 (확장성 확보)
    - 여러 서버가 있을 경우 Nginx가 자동으로 트래픽 분산
    - 현재는 단일 서버지만 추후 확장에 대비한 구조 설계 가능

---

4. 보안 및 방화벽 기능
    - 특정 IP 차단, 경로 제한 등 다양한 접근 제어 가능
    - 외부에는 80/443 포트만 열고 내부 서버 포트는 숨김
    - DDOS 방어, HTTPS 강제 리디렉션 등의 기능 활용 가능

---

5. 정적 파일 처리
    - HTML, 이미지, JS, CSS 등 정적 리소스를 빠르게 서빙
    - 프론트엔드가 분리된 구조에서 특히 유용
    - 현재는 백엔드 중심이지만 향후 프론트 분리 시 활용 가능

---

### 3. 단점 / 주의사항

- 동적 요청 처리 불가
    - Java, Spring 같은 서버 없이 직접 로직 실행은 불가능

- 에러 핸들링 제한
    - 복잡한 조건 분기나 예외 처리는 백엔드 서버에서 별도로 처리 필요

- 세션 관리 기능 없음
    - 상태 없는 서버 구조이므로 로그인/세션은 Redis 등으로 별도 구성 필요

---

### 4. 도입 배경과 필요성

우리 프로젝트는 외부 통신, 보안, 분산 처리를 위한 웹 서버 구성이 필요하며  
사용자의 동시 접속이 많이 발생할 수 있는 구조입니다.  
이에 따라 동시 처리에 강점을 가진 Nginx를 도입하게 되었습니다.

</details>

<details>

<summary> Redis를 캐시 라이브러리로 선택한 이유 </summary>

### [ Redis ]
>캐시 및 실시간 처리용 인메모리 데이터 저장소로 사용 예정

### 1. 기술 설명

Redis는 인메모리 기반의 고성능 Key-Value 저장소로,  
단순한 캐시를 넘어서 다양한 자료 구조와 기능을 제공하는 데이터 구조 서버입니다.

---

### 2. 기술 장점

1. 다양한 자료 구조 지원
    - key-value 외에도 List, Set, Sorted Set, Hash, Bitmap 등 다양한 구조 지원
    - 추천 시스템, 중복 검사, TTL 기반 토큰 저장 등 복잡한 비즈니스 로직에 유연하게 대응 가능

---

2. 빠르고 안정적인 성능
    - 데이터를 메모리에서 직접 처리 → 응답 속도가 매우 빠름
    - 캐시 히트율 향상을 통해 전체 시스템 부하 감소 및 성능 최적화

---

3. 운영 편의성 및 확장성
    - TTL(만료), Pub/Sub, HyperLogLog 등 다양한 운영 기능 제공
    - 클러스터 모드, Sentinel 구성 지원 → 고가용성 및 수평 확장 용이
    - 장애 복구에 유리하고 실무에서도 안정적으로 적용 가능

---

4. Spring 연동 및 커뮤니티
    - Spring Data Redis, Redisson 등과의 통합이 잘 되어 있음
    - 공식 문서, 오픈소스 예제가 많아 적용과 유지보수가 쉬움

---

### 3. 단점 / 주의사항

- 메모리 기반 저장소이므로 용량 한계 존재
    - 모든 데이터를 RAM에 저장 → 대용량일수록 비용 부담
    - 용량 초과 시 데이터 손실 또는 OOM 위험 가능

- 데이터 영속성이 제한적
    - RDB, AOF 방식이 있긴 하지만 디스크 기반 DB보다는 안정성 낮음
    - 장애 발생 시 일부 데이터 유실 가능성 있음

- 복잡한 연산에는 부적합
    - 단일 키 기반 연산에는 강하나, 복잡한 join, 조건문, 트랜잭션은 제약이 있음
    - 조회용 데이터베이스로 사용하기에는 한계가 있음

---

### 4. 도입 배경과 필요성

우리 팀은 다음과 같은 이유로 Redis를 도입하였습니다.

- 단순 캐시 이상의 활용 가능성 (랭킹, 중복 제어, TTL 등 실시간 처리)
- 다양한 도메인 요구사항에 대한 유연한 대응
- Spring 기반 서비스와의 연동이 편리하고 학습 자료가 풍부
- Hazelcast, Memcached 등 대안들과 비교했을 때 학습 비용, 기능 확장성, 생태계 측면에서 가장 적합

---

### 대안 기술 비교 요약

| 항목     | Memcached       | Hazelcast    | Redis                  |
|--------|-----------------|--------------|------------------------|
| 자료 구조  | key-value 단일 구조 | 일부 복잡한 구조 지원 | 다양한 자료 구조 지원           |
| 언어 지원  | 제한적             | Java 중심      | 거의 모든 언어 지원            |
| 사용 편의성 | 간단하나 기능 제한적     | 설정과 구현 복잡    | Spring과의 통합 및 자료 풍부    |
| 성능     | 매우 빠름           | 고정적          | 빠르고 유연                 |
| 확장성    | 낮음              | 분산 구조 우수     | 클러스터 / Sentinel 구성 유연함 |

</details>

<details>
<summary> 도메인 주도 설계 (DDD: Domain-Driven Design) 도입 배경 및 선택 이유 </summary>

### [ DDD: Domain-Driven Design ]

---

### 1. 기술 설명

DDD는 도메인(비즈니스 핵심 영역)을 중심으로 소프트웨어를 설계하는 방식입니다.  
기능 분할 중심의 단순한 계층 구조를 넘어 도메인 모델과 경계(Bounded Context)를  
명확히 하여 복잡한 비즈니스 로직을 효과적으로 분리하고 유지보수하기 위한  
설계 철학입니다.

---

### 2. 기술 장점

1. 도메인 중심의 구조화
    - ERD 기반이 아닌 비즈니스 개념 중심의 모델 구성
    - 복잡한 로직을 애그리거트 단위로 묶어 응집도 향상

---

2. 경계(Bounded Context) 설정을 통한 독립성 확보
    - 각 도메인을 독립된 컨텍스트로 분리
    - 도메인 간 의존은 API나 이벤트로 통신 → 결합도 감소

---

3. 유지보수성과 테스트 용이성 향상
    - 도메인마다 로직이 명확히 분리되어 있어 변경 영향 최소화
    - 응용 서비스와 도메인 모델이 분리되어 단위 테스트 작성이 용이

---

4. 명확한 트랜잭션 경계 설정
    - 애그리거트 루트를 통해서만 변경 → 일관성 있는 트랜잭션 관리

---

5. 리포지토리 및 계층 구조의 정립
    - 애그리거트 단위 저장 → 불필요한 저장소 호출 최소화
    - 표현-응용-도메인-인프라의 계층적 구조에 맞춘 DIP 원칙 적용 가능

---

### 3. 단점 / 주의사항

- 초기 설계 비용 존재
    - 도메인 분석과 설계에 시간이 소요됨
    - 작은 프로젝트나 MVP 단계에선 오히려 오버엔지니어링 될 수 있음

- 개발자 간 개념 정립 필요
    - DDD 용어(Bounded Context, Aggregate 등)의 명확한 이해 필요

- 복잡한 기능은 여전히 도메인 서비스 등에서 조정이 필요
    - 모든 로직을 엔티티에 담으려 하면 과도하게 무거워질 수 있음

---

### 4. 도입 배경과 필요성

우리 팀은 다음과 같은 판단 아래 DDD를 도입하기로 하였습니다.

- 기능별 레이어 설계의 한계
    - 도메인 간 의존성과 강한 결합 발생 → 예기치 못한 변경 전파
    - 객체 그래프 탐색 증가 → N+1, 불필요한 fetch 등 성능 이슈 발생

- 비즈니스 복잡도 증가
    - 참여, 리뷰, 추천, 필터링 등 도메인 간 상호작용이 증가하는 구조
    - 초기 설계 이후 확장성 고려가 필요한 시점

- 도메인 경계와 책임 분리가 명확하지 않음
    - "이 로직은 어디에 있어야 하지?"라는 판단이 반복됨
    - 유지보수/협업 과정에서 의사결정 피로 증가

- 루트 애그리거트를 통한 하위 객체 일관성 관리
  - 루트 애그리거트에서만 하위 엔티티를 변경하도록 하여 일관성 있는 도메인 모델을 유지
    - 무분별한 직접 접근을 방지하고 변경 책임을 명확히 구분

- 인터페이스 기반의 추상화를 통한 DIP 원칙 적용
  - 구현이 아닌 추상화에 의존하도록 설계하여 유연한 구조 확보
    - 도메인 계층이 인프라 세부 구현에 종속되지 않도록 방지

</details>

<details>
<summary> Kafka 도입 배경 및 선택 이유 </summary>

### [ Kafka ]

---

### 1. 기술 설명

Kafka는 대용량 데이터의 비동기 메시지 전송을 지원하는 분산 메시지 브로커입니다.
다른 시스템과의 느슨한 연결(loose coupling)을 가능하게 하며, 실시간 로그 수집,
알림 처리, 데이터 파이프라인 등 다양한 이벤트 기반 아키텍처에 활용됩니다.

---

### 2. 기술 장점

1. 서비스 결합도 감소
- 이벤트 기반으로 도메인 간 의존도를 낮춤
- 직접 호출이 아닌 메시지 전달 방식으로 기능 연결

---

2. 비동기 이벤트 처리
    - 요청-응답 구조가 아닌 비동기 방식으로 시스템 부담 완화
    - 알림, 로그 저장 등 사용자 응답과 무관한 처리에 적합

---

3. 메시지 영속성 보장
    - Kafka는 디스크 기반 로그 저장으로 메시지를 유실 없이 유지
    - 장애 발생 시에도 메시지 재처리가 가능

---

4. 운영 및 관찰성 향상
    - Kafka UI, Control Tower 등을 통해 메시지 흐름 시각화 및 모니터링 가능
    - 장애 추적 및 디버깅 용이

---

5. WebSocket 알림 연동 용이
    - 실시간 알림 기능을 Kafka → WebSocket 서버 구조로 쉽게 확장 가능
---

### 3. 단점 / 주의사항

- 학습 곡선
    - Kafka 기본 개념, 설정, 메시지 처리 흐름 학습 필요
    - 초기 환경 구성에 진입장벽 존재

---

- 운영 리소스 부담
    - Zookeeper, Broker, Consumer group 등 인프라 구성 및 유지 필요
    - 모니터링, 배포 자동화 등 추가 관리 요소 발생

---

- 메시지 처리 실패 시 예외 관리
    - 역직렬화 실패, 중복 처리, 처리 지연 등 장애 발생 가능
    - 처리 실패에 대비한 재처리 로직 또는 DLQ(Dead Letter Queue) 구성 필요

---

- 오버엔지니어링 우려
    - 현재 서비스는 단일 서버 기반의 모놀리식 구조로 Kafka 도입이 복잡도 증가로 이어질 수 있음
    - 단기적 기능만 본다면 Spring Event로도 처리 가능

---

### 4. 도입 배경과 필요성

우리 팀은 다음과 같은 판단 아래 Kafka를 도입하기로 하였습니다.

- 확장성 측면
    - 현재 프로젝트는 Spring Event를 활용한 내부 이벤트 처리 방식을 사용하고 있지만, 트랜잭션에 종속되고, 테스트 및 확장성 측면에서 한계가 있습니다.
    - 향후 MSA 구조로의 전환 가능성을 고려하여, 대규모 비동기 이벤트를 안정적으로 처리할 수 있는 Kafka를 선택하였습니다.
- 운영 및 관찰성 향상
    - Kafka를 통해 알림 서비스, 사용자 행동 분석, 로그 수집 등 다양한 기능을 느슨하게 연결할 수 있습니다.

종합적으로, Kafka는 높은 확장성과 낮은 결합도를 바탕으로 시스템의 유연성을 높이고, 장애 복원력을 강화할 수 있을 것으로 기대하고 있습니다.

</details>

<details>
<summary> Elasticsearch 도입 배경 및 선택 이유 </summary>

### [ Elasticsearch ]

---

### 1. 기술 설명

Elasticsearch는 분산형 검색 엔진으로 구조화되지 않은 텍스트 데이터를 고속으로 검색하고 분석하는 데 특화된 기술입니다.
정형/비정형 데이터를 JSON 기반으로 저장하며 강력한 검색 쿼리 기능과 실시간 검색 성능을 제공하여 추천 기능, 사용자 로그 분석, 시각화 기반 모니터링 등에 활용할 수 있습니다.

---

### 2. 기술 장점

1. 고성능 실시간 검색
- 수십만 건 이상의 데이터도 밀리초 단위로 검색 가능
- RDB 대비 텍스트 검색에 최적화 (역색인 기반 구조)

---

2. 강력한 쿼리 표현력
- 다중 필터, 범위 필터, 자동완성, 검색어 강조 등 복잡한 검색 쿼리 지원
- `BM25` 기반 유사도 정렬로 추천 기능 구현 가능

---

3. 유사도 기반 추천 기능 구현 용이
- 참여 이력, 관심 태그, 지역, 기술 스택 등을 기반으로 유사도가 높은 게시글 추천 가능 (태그 기반 유사도 분석 등)

---

4. 시각화 연동 가능 (Kibana)
- Kibana와 연동 시 실시간 검색 성능, 통계 지표, 사용자 활동 로그 등을 GUI로 시각화 가능

---

5. 데이터 분석 및 로그 기반 확장 가능성
- 향후 사용자 행동 분석, 추천 알고리즘 고도화, 로그 기반 트래픽 분석 등
  다양한 확장 기능에 활용 가능
---

### 3. 단점 / 주의사항

- 트랜잭션 미지원
    - RDB처럼 multi-row 트랜잭션이나 rollback이 불가능
    - 데이터 정합성을 위해 동기화 전략 필요 (ex: Outbox 패턴 + Kafka)

---

- 쓰기 비용 증가
    - 문서 수정이 잦을 경우 인덱스 재작성 비용이 발생하여 성능 저하 우려

---

- 운영 복잡도
    - 샤드, 레플리카, 인덱스 관리 등 구성 요소가 많고 설정이 복잡할 수 있음
    - 디스크 사용량 증가 가능성

---

- 인덱싱 전략 필요
    - 게시글처럼 자주 수정되는 데이터는 인덱싱에 부적합할 수 있음
    - 인덱스는 수정이 적고 검색이 잦은 데이터에 우선 적용

---

- 같은 데이터를 두 개의 DB에 저장해야함
    - 쓰기를 진행하는 테이블과 해당 데이터를 복제하여 색인기능 활용할 엘라스틱서치 DB도 따로 존재해야함
    - 중복데이터로 인한 DB 사용량 증가

---

### 4. 도입 배경과 필요성

우리 팀은 다음과 같은 판단 아래 Elasticsearch를 도입하기로 하였습니다.

- Elasticsearch는 고성능 검색, 로그 기반 분석 등을 하나의 시스템에서 통합적으로 지원할 수 있어 단순 검색 엔진을 넘어 분석 기반 인프라로 활용 가능
- Kibana를 활용한 시각화 기능과 함께 성능 모니터링 지표, 트래픽 분석, 사용자 로그 추적 등 운영적 이점도 확보할 수 있어 서비스 확장성과 운영 효율성 확보

</details>

<details>
<summary> mongoDB 도입 배경 및 선택 이유 </summary>

### [ Elasticsearch ]

---

### 1. 기술 설명

MongoDB는 NoSQL 데이터베이스입니다.
JSON 기반의 유연한 스키마 구조를 제공하여 정형/비정형 데이터를 모두 저장할 수 있습니다.
테이블 대신 Document 구조를 사용하며, Read/Write 성능이 뛰어납니다.

---

### 2. 기술 장점

1. 스키마 유연성
- 필드를 선택적으로 추가 가능
- 데이터 변경이 잦은 서비스에 적합

---

2. 비정형 데이터 저장 용이
    - 사진, 영상 경로, 메타데이터 등을 문서 내 필드로 바로 저장 가능
    - RDB의 테이블 정규화 없이 단일 Document에 직관적으로 표현 가능

---

3. 확장성
    - 필요 시 Sharding 구조로 수평 확장이 가능
    - 대규모 사용자/데이터 증가에도 안정적인 성능 유지
---

### 3. 단점 / 주의사항

- 트랜잭션 제약
- 단일 Document 수준의 원자성은 강력하지만
다중 Document 트랜잭션은 성능 부담이 있어 사용 시 주의 필요

---

- 운영 복잡성
    - Replica Set, Sharding을 운영할 경우 설정과 모니터링 부담 증가
    - 네트워크 지연, 선출 과정에서 잠시 쓰기 불가 상태가 발생할 수 있음

---

- 메모리 사용량
    - 인덱스 크기와 캐시 메모리 사용량이 커질 수 있음
    - 대규모 데이터 환경에서는 서버 리소스 관리 필수
---

### 4. 도입 배경과 필요성

우리 팀은 다음과 같은 이유로 MongoDB Replica Set(3노드) 구조를 도입하였습니다.

- 후기 데이터의 비정형성
    - 텍스트(제목, 내용)만 있는 경우도 있고, 사진/영상이 포함될 수도 있음
    - 유연한 스키마를 통해 단일 Document로 표현 가능
- RDB의 제약
    - 후기별 첨부파일 테이블을 별도로 두면 관리 복잡도 상승
    - 정규화/조인 비용 증가 → 조회 성능 저하 우려
- 안정적인 운영 요구
    - 단일 서버 MongoDB는 장애 발생 시 서비스 중단 위험이 큼
    - Replica Set(3노드) 도입으로 Primary 장애 시 자동 Failover 지원
    - Secondary를 통한 데이터 복제 및 읽기 분산 가능
    - 데이터 무결성 보장 - Replica Set 환경에서는 트랜잭션도 지원
- 확장 및 유지보수성
    - 샤딩 확장을 고려할 수 있어 장기적으로 대규모 데이터에도 대응 가능
</details>

<details>
<summary> OAuth 도입 배경 및 선택 이유 </summary>

### [ OAuth ]

---

### 1. 기술 설명

OAuth는 인터넷 사용자들이 비밀번호를 제공하지 않고 다른 웹사이트 상의 자신들의 정보에 대해 웹사이트나 애플리케이션의 접근 권한을 부여할 수 있는 공통적인 수단으로서 사용되는 접근 위임을 위한 개방형 표준입니다. 

---

### 2. 기술 장점

1. 보안성 강화
    - Access Token/Refresh Token 기반 인증
    - PKCE 적용 시, Authorization Code 탈취 방지 가능
    - 클라이언트 비밀(Client Secret) 없이도 안전한 인증 구현

---

2. 외부 서비스 연동 확장성
    - Google, Kakao, Apple 등 OAuth 제공 서비스 연동 가능

---

3. 사용자 정보 저장 최소화
    - 자체 DB에 패스워드 저장 없이 인증 처리
    - 개인정보 유출 리스크 감소
---

### 3. 단점 / 주의사항

- 구현 복잡도
    - OAuth 흐름에 대한 명확한 이해와 예외 처리 로직 필요
    - 특히 리다이렉션 흐름, PKCE 처리, 토큰 저장 및 갱신 등 구조적 고민이 필요

---

- 인증 서버 연동 제약
    - 자체 인증 서버를 둘 경우 보안성 확보를 위한 고급 설정 필요
    - 외부 서비스(Google 등)에 의존 시 서비스 장애에 대한 대응 전략 필요
---

### 4. 도입 배경과 필요성

우리 프로젝트는 다음과 같은 판단 아래 OAuth 를 도입하고자 했습니다.

- 기존 서비스는 자체 로그인만을 통해 인증을 처리
- 추후 외부 서비스(Google 등)와의 연동 및 확장성을 고려
- 사용자가 별도의 가입 과정 없이 손쉽게 로그인 할 수 있어 서비스의 진입 장벽을 낮춰 유입률을 높이는데 도움이 됨
</details>



## ⚠️ 트러블 슈팅
<details>

<summary> 🧪 통합 테스트 코드 트랜잭션 관련 실패 </summary>

### 🧪 통합 테스트 코드 트랜잭션 관련 실패
#### 문제 상황

---

- `@TransactionalEventListener(phase = BEFORE_COMMIT)`를 사용하는

  이벤트 리스너가 테스트에서 동작하지 않음

- `ApplicationEventPublisher.publishEvent()`를 호출했지만

  이후 `UserReader`를 통해 유저를 조회했을 때 저장된 결과가 없음


---

- `@TransactionalEventListener(phase = BEFORE_COMMIT)` 는

  트랜잭션 커밋 직전에 실행된다.

- 이벤트가 트랜잭션 범위 내에서 퍼블리시되어야 작동한다.

---

#### 실패한 테스트 예시

---

```
java
@Test
@Transactional
@Commit
void 회원가입_이벤트_퍼블리시() {
    eventPublisher.publishEvent(...);
}
```

---

#### 실패 원인

---

1. `eventPublisher.publishEvent()`가 트랜잭션 범위 내에서 실행된 것처럼 보이지만

   내부적으로 즉시 동기 실행되며 트랜잭션과 연동되지 않음

2. `@TransactionalEventListener(phase = BEFORE_COMMIT)`는 스프링이 관리하는

   트랜잭션 커밋 시점에 반응하므로 이벤트가 이보다 먼저 퍼블리시되면 리스너가

   실행되지 않음

3. 테스트 메서드 자체에 `@Transactional`과 `@Commit`이 붙어 있어도

   이벤트 퍼블리시 시점이 커밋 직전이 아님 → 리스너 작동 안 함


---

#### 해결 방법

---

- 트랜잭션 범위 내에서 퍼블리시하는 별도 서비스 메서드 호출

```
java
@Transactional
public void publishSignUpEvent(SignupRequest request) {
    publisher.publishEvent(...);
}
```

- 테스트에서는 해당 메서드를 호출

```
java
@Test
void 회원가입_이벤트_후_조회() {
    testEventPublisher.publishSignUpEvent(request);
    ...
}
```

- 이벤트가 이미 트랜잭션 범위 안에서 퍼블리시
- 커밋 시점에 `BEFORE_COMMIT` 리스너가 정상적으로 실행됨
- DB에 유저 데이터가 실제로 커밋됨 → 이후 조회 성공

---

#### 대안

---

1. `@TransactionalEventListener(fallbackExecution = true)` 사용

- 트랜잭션이 없어도 리스너 실행되도록 설정
- 테스트 용도로는 유용할 수 있으나, 실서비스와 동일한 조건이 아님
- 실제 환경과 동작 방식이 달라져 테스트 신뢰도 저하 가능

2. `@Transactional(propagation = Propagation.REQUIRES_NEW)` 적용

- 이벤트 처리 로직을 별도 트랜잭션으로 강제 실행 가능
- 하지만 `BEFORE_COMMIT` 의미가 퇴색 될 수 있어 주의 필요

---

#### 결론

---

- `@TransactionalEventListener(phase = BEFORE_COMMIT)`는 반드시 트랜잭션

  내부에서 퍼블리시된 이벤트에만 반응한다.

- 테스트에서 이를 확인하고 싶다면 이벤트 퍼블리시를 별도의 서비스 계층에서

  수행해야 한다.

- `BEFORE_COMMIT` 일때는 단순히 테스트 메서드에서 `@Transactional`과 `publishEvent()`를 호출하는 것만으로는 충분하지 않다.

</details>


<details>

<summary> ⚖️ CQRS 책임 분리 위반에 따른 구조 개선 </summary>

### ⚖️ CQRS 책임 분리 위반에 따른 구조 개선
#### 문제 발단

---

- 우리 프로젝트는 CQRS 패턴을 사용하여 Commander와 Reader를 구분하여 사용하기로 결정함.
- 현재는 하나의 DB를 사용 중이지만 구조적으로 Commander와 Reader를 분리하여 서비스별 기능을 명확히 분리 
- 추후 DB 분리에 대비하고자 하는 목적

- 하지만 실제 구현시 Commander 서비스가 Reader를 통해 데이터를 조회하여 수정·생성·삭제 작업을 수행하는 상황이 발생함.

#### 문제 이유

---

- 현재는 단일 DB를 사용하고 있지만 CQRS 원칙에 따라 책임을 명확히 분리한 이상 Reader는 조회 전용, Commander는 쓰기 전용으로 사용되어야 함.
- 추후 실제로 DB가 분리될 경우, Reader는 지연된 데이터나 복제본을 참조할 수 있음
- 이 상태에서 변경 작업을 수행하면 데이터 정합성에 심각한 문제가 발생할 수 있음

#### 해결

---
- 이러한 문제점에 대해 팀원들에게 설명하고 쓰기 작업이 필요한 경우에는 반드시 Commander 내에서 조회하도록 코드 구조를 변경.
- 이로써 책임의 경계를 명확히 하고 향후 DB 분리 시 발생할 수 있는 정합성 문제를 사전에 방지할 수 있게 됨.

</details>

<details>

<summary> ⛓️ JPA MultipleBagFetchException 해결: 다중 컬렉션 패치 조인 문제 </summary>

### ⛓️ JPA `MultipleBagFetchException` 해결: 다중 컬렉션 패치 조인 문제
#### 문제 발단

---

JPA에서 `User` 엔티티를 조회할 때 `userHobbies`와 `userTeckStacks` 두 개의 연관 컬렉션을 패치 조인(fetch join) 하여 한 번에 가져오도록 설정함

```java
List<User> users = queryFactory
        .selectFrom(user)
        .leftJoin(user.userHobbies).fetchJoin()
        .leftJoin(user.userTeckStacks).fetchJoin()
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
```

이와 같이 실행 시 다음과 같은 예외가 발생했음

```
org.hibernate.loader.**MultipleBagFetchException**:
**cannot simultaneously fetch multiple bags:**
[User.userHobbies, User.userTechs]
```

---

#### 문제 이유

---

- JPA(Hibernate)는 두 개 이상의 `List` 타입 컬렉션을 동시에 fetch join 할 경우

  내부적으로 카테시안 곱(Cartesian Product) 이 발생

- `List` 타입은 Hibernate 내부적으로 `Bag` 타입으로 처리되는데 Bag은 중복을

  허용하므로 Hibernate가 이를 정리할 수 없어 예외를 발생시킴

- 예를 들어 유저 1명이 취미 3개, 기술 2개를 가진다면 결과는 3 × 2 = 6개의 레코드로

  늘어나게 되며 이 중복을 관리하지 못함


---

#### 문제 해결

---

해결 방법: `List` → `Set` 변경

- `Set`은 중복을 허용하지 않기 때문에 Hibernate가 결과 데이터를 적절히 처리가능
- 엔티티에서 다음과 같이 타입을 변경

```java
// --- Before ---
@OneToMany(mappedBy = "user")
private List<UserHobby> userHobbies;

@OneToMany(mappedBy = "user")
private List<UserTech> userTechs;

// --- After ---
@OneToMany(mappedBy = "user")
private Set<UserHobby> userHobbies;

@OneToMany(mappedBy = "user")
private Set<UserTech> userTechs;
```

---

#### 실행 결과

---

- 더 이상 `MultipleBagFetchException` 예외가 발생하지 않고 원하는 데이터가

  패치 조인을 통해 정상적으로 조회됨

</details>

<details>

<summary> 📑 JPA 페이징 + Fetch Join 시 발생하는 메모리 페이징 이슈 </summary>

### 📑 JPA 페이징 + Fetch Join 시 발생하는 메모리 페이징 이슈
#### 문제 발생

---

`userHobbies`와 `userTeckStacks` 을 조회하는 과정에서 N+1 문제를 해결하기 위해

각각 `fetch join`과 페이징 처리를 적용함

실행은 되지만 다음과 같은 경고 메시지가 출력됨

```
firstResult/maxResults specified with collection fetch; **applying in memory**
```

> 컬렉션을 패치 조인하면서 페이징(offset, limit)을 설정했기 때문에 메모리 내에서 페이징이 수행된다는 의미
>

---

#### 원인 파악

---

Hibernate는 컬렉션(`@OneToMany`, `@ManyToMany`)에 대해 `fetch join`이 적용된 경우

- DB 레벨에서 `limit`, `offset`을 이용한 페이징이 불가능
    - 카테시안 곱 + 중복 제거 과정이 복잡하여 DB에서 정확한 row 제한이 어려움
- 결국 Hibernate는 모든 결과를 메모리에 로딩한 후, 그 안에서 페이징을 적용함
- 이로 인해 성능 저하 및 메모리 낭비 발생 가능

---

#### 해결 방법

---

1. QueryProjection으로 fetch join할 필드(`userHobbies`,`userTeckStacks`) 제외 후, `User` 엔티티만 페이징으로 먼저 조회
2. 조회한 User의 ID 리스트를 추출하여, 해당 ID에 속한 취미와 기술 스택을 조회
3. 취미와 기술 스택 조회 결과를 `UserResponse` DTO에 매핑

> select 시에는 페이징 성능을 확보하고 그 이후에 in절 + join 없이 필요한 데이터만
>
>
> 가져오는 방식으로 해결
>

```
java
List<UserResponse> contents = queryFactory
        .select(new QUserResponse(...)) // fetch join 필드 제외
        .from(user)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();
        
// user ID 리스트 추출
List<Long> userIds = users.stream().map(UserResponse::getId).toList();

// <User ID, 취미/기술스택 리스트> 형식으로 취미와 기술 스택 조회
Map<Long, List<String>> hobbyMap = ...
Map<Long, List<String>> techStackMap = ...

for (UserResponse dto : users) {
		// 각 UserResponse DTO에 취미와 기술 스택 조회 결과 매핑
		List<String> hobbyList = hobbyMap.getOrDefault(dto.getId(), Collections.emptyList());
    List<String> techList = techStackMap.getOrDefault(dto.getId(), Collections.emptyList());
		dto.setHobbyAndTech(hobbyList, techList);
}
```

---

#### 실행 결과

---

- Hibernate 경고 사라짐
- DB에서 페이징 정상 적용됨
- `IN` 절 기반 쿼리로 N+1 없이 연관 데이터도 효율적으로 조회됨

</details>


<details>
<summary> 🔒 @Transactional ,@DistributedLock 동시 사용 시 분산락 무효화 </summary>

### 🔒 @Transactional ,@DistributedLock 동시 사용 시 분산락 무효화
#### 문제 상황

---

다음과 같이 `@Transactional`과 `@DistributedLock` 어노테이션을 함께 사용하는 메서드에서 분산락이 제대로 동작하지 않는 문제가 발생

```java
@Transactional
@DistributedLock(
    key = "#postId",
    timeoutMs = 5000,
    leaseTimeMs = 3000
)
public void applyForParticipation(Long postId) {
    // 참가 신청 로직
}
```

---

#### 원인 분석

---

1. Spring AOP의 어드바이스 실행 순서 불확실

- Spring은 여러 AOP 어노테이션(`@Transactional`, `@DistributedLock`)을 동시에 사용할 때 실행 순서를 보장하지 않는다.
- 특정 어노테이션이 먼저 적용될지 여부는 프록시 설정 및 어드바이스 우선순위 설정에 따라 달라질 수 있다.

---

2. 실행 순서에 따라 발생하는 문제

`@Transactional`이 먼저 실행되면 다음과 같은 순서가 됨

```
1. 트랜잭션 시작
2. 분산락 획득
3. 비즈니스 로직 실행
4. 분산락 해제 ⚠️
5. 트랜잭션 커밋
```

- 문제점: 분산락은 해제되었지만 트랜잭션이 아직 커밋되지 않은 상태이다.
- 이 상태에서 다른 스레드가 락을 획득하면 이전 변경사항이 반영되지 않은 데이터를

  참조하게 되어 정합성 문제가 발생한다.


---

3. **올바른 실행 순서**

`@DistributedLock` → `@Transactional` 순으로 적용되어야 안전하다.

```
1. 분산락 획득
2. 트랜잭션 시작
3. 비즈니스 로직 실행
4. 트랜잭션 커밋
5. 분산락 해제 ✅
```

- 트랜잭션 커밋이 완료된 이후에 락이 해제되므로 정합성이 보장된 상태에서 다음 요청이 처리됨


---

#### 해결 방법

---

- `@DistributedLock`이 `@Transactional`보다 먼저 실행되도록 AOP 우선순위 설정 (`@Order`) 을 명시적으로 지정
- 분산락 로직을 별도의 서비스로 분리하여 트랜잭션 내부가 아닌 락 획득 후 명시적으로 트랜잭션을 시작하는 방식으로 구조를 재설계

</details>


## 📈 성능 개선
<details>

<summary> 📀 캐시로 조회 성능 개선하기 </summary>

### 문제 정의

---

1. 주 기능이 게시글인 만큼, 타 기능보다 게시글을 조회하는 경우가 많음
2. 같은 게시글을 여러 번 조회할 때마다 DB에 접근하기 때문에 과부하가 쉽게 발생
    - 조회 시간이 증가에 따라 사용자가 서비스를 이용하는 데 불편함을 겪을 수 있음

---

### 해결 방법
Redis 캐시를 사용하여 조회

- Redis는 빠르고 안정적이면서, 다양한 자료형을 제공함
- 로그아웃이나 동시성 제어, 채팅방 등 Redis를 사용하는 부분이 많음

|        | **Memcached**                                        | **Hazelcast**                 | **Redis**                                 |
|--------|------------------------------------------------------|-------------------------------|-------------------------------------------|
| **장점** | key - value의 단순한 구조를 가지기 때문에 매우 빠름                   | - 자바 친화적 <br> - 다양한 분산 구조를 제공 | - 다양한 자료 구조와 언어를 지원 <br> - 빠르고 안정적임       |
| **단점** | - 리스트, Sorted set같은 복잡한 자료구조를 가지지 못함  <br>- 기능이 제한적임 | - 구현이 어려움  <br>- 학습 곡선이 존재    | - 자바 기능은 부족함  <br> - 클러스터 구성이나 운영 복잡성이 존재 |

---

### 구현 내용
- Redis 캐시에 같은 요청 데이터가 존재하는지 확인
    - 없으면, 기존처럼 DB에서 데이터를 조회
    - 있으면, Redis 캐시에서 조회해서, DB에 접근하지 않음

  > 캐시 적용 메서드
  >
  > - 게시글 단건 조회 `findPost`
  > - 게시글 검색 조회 `getPostDtosBySearch`
  > - 내 성사된/신청한 게시물 조회 `findMyConfirmedPosts`
  > - 내가 작성한 게시물 조회 `findMyWrittenPosts`
  > - 추천 게시물 조회 `getSuggestedPosts`
  > - 내가 좋아요 누른 게시물 조회 `findMyLikedPosts`

- 데이터 변경 요청이 들어오게 되면, `@CacheEvict` 로 관련 캐시 데이터를 삭제하여 DB 데이터와 일치시킴
    - ex. 게시글 삭제 및 수정, 게시글 좋아요, 참가 등

<aside>

`실행 흐름`

![실행흐름.png](docs/image/실행흐름.png)

</aside>

---

### 결과 및 효과

---

실제로 캐시가 조회 성능을 개선했는지 확인하기 위해, nGrinder로 부하 테스트를 진행함

> 1분 동안 50명이 지속적으로 요청을 보내서, 얼마나 요청을 처리할 수 있는지, 하나의 요청을 처리하는 데 몇 초가 걸리는지 확인
>
- 캐시를 적용하게 되면, 전반적으로 **처리량(TPS)이 증가**하고 **평균 테스트 시간(Mean Test Time)이 감소**하는 것을 볼 수 있습니다.
    - TPS는 **약 300% 증가**
    - 최고 TPS는 **약 263% 증가**
    - 평균 테스트 시간은 **약 75% 감소**
    - 실행된 테스트 수는 **약** **318% 증가**

  |                  | **캐싱 적용 안함 V1** | **캐싱 적용함 V2** |
  |------------------|-----------------|---------------|
  | TPS              | 532.4           | 2,129.9       |
  | Peak TPS         | **759**         | **2,755**     |
  | Mean Test Time   | **94.41 ms**    | **23.17 ms**  |
  | Executed Tests   | **27,517**      | **115,523**   |
  | Successful Tests | 27,517          | 115,523       |
  | Errors           | 0               | 0             |

<aside>

**`처리량(TPS)`**

![처리량비교.png](docs/image/처리량비교.png)

**`평균 테스트 시간(Mean Test Time)`**

![평균테스트시간.png](docs/image/평균테스트시간.png)

</aside>

---

### 회고
- 캐시 삭제 시, 변경된 데이터가 포함된 캐시만 삭제하는 것이 아닌, 모든 캐시를 삭제함
    - 불필요하게 많은 캐시를 비우게 되는 것 같아, 캐시를 세분화해서 삭제하려 했지만,
      숙련도 및 시간 부족으로 그대로 전체 삭제를 진행함
- 시간적 여유가 있다면 추후 개선할 의향 있음
</details>


<details>
<summary> 🔒 분산락 제어 </summary>

### 문제 정의

---
1. 서비스의 핵심 기능인 프로모션**/게시글 선착순 신청 및 취소** 로직에서 **동시에 다수의 요청**이 몰릴 수 있음
2. DB 단에서만 처리할 경우,
    - 정원 초과 발생 (중복 신청 허용)
    - 동시에 취소 및 수락 시 데이터 꼬임 발생
    - Deadlock 혹은 Transaction Rollback으로 불필요한 비용 발생
---

### 해결 방법

---
**Redisson 기반 분산 락 + AOP 적용**

- Redisson은 Redis를 활용하여 **분산 환경에서도 동시성 제어가 가능한 락(Lock) 메커니즘**을 제공
- AOP를 통해 특정 메서드 단위로 **자동 락 관리(획득/해제)**를 적용 → 코드 간결성 및 유지보수성 향상

|        | **MySQL FOR UPDATE**              | **낙관적 락 (Optimistic Lock)**           | **Redisson 분산락**                           |
|--------|-----------------------------------|---------------------------------------|--------------------------------------------|
| **장점** | - DB에서 바로 지원<br>- 단순하고 빠른 도입      | - 충돌이 적은 환경에서는 효율적 <br>- 별도 인프라 필요 없음 | - 분산 환경에서도 안전 <br> - 재진입, 공정 락 등 다양한 기능 제공 |
| **단점** | - DB 부하 증가 <br> - Deadlock 가능성 존재 | - 충돌이 많으면 성능 저하 <br>- 재시도 로직 필수       | - Redis 의존성 발생 <br>- 운영 복잡성 존재             |

---

### 구현 내용

---

- 공통 AOP 어노테이션 작성
    - `@DistributedLock(key = "post:#{postId}")` 와 같은 방식으로 선언
    - 메서드 실행 전 Redisson 락을 획득 → 메서드 실행 → 종료 후 락 해제
- Redisson의 `RLock`을 활용해 **분산락** 구현
- 락 적용 메서드 예시
    - 프로모션 / 선착순 게시글 참가 신청
    - 참가 취소
    - 참가 승인/거절

> 실행 흐름
>
> 1. 요청 진입
> 2. AOP Interceptor가 Redisson을 통해 락 획득 시도
> 3. 락 성공 시, 비즈니스 로직 실행
> 4. 로직 종료 후 자동 락 해제
> 5. 락 실패 시, 예외 반환 or 재시도

---

### 결과 및 효과

---

동시성 제어가 실제로 정합성과 성능에 어떤 영향을 주는지 확인하기 위해 k6로 부하 테스트를 진행함

- **Before (락 없음)**

  ![before-lock-1.png](docs/image/before-lock-1.png)

  ![before-lock-2.png](docs/image/before-lock-2.png)

    - 정원 50명 이벤트에 200명 동시 신청 시 → 53**명 신청됨 (데이터 불일치 발생)**
- **After (Redisson + AOP 적용)**

  ![after-lock.png](docs/image/after-lock.png)

    - 정원 50명 이벤트에 200명 동시 신청 시 → 5**0명 정확히 신청됨 (데이터 정합성 보장)**
    - TPS 변화는 미미하지만, **Rollback/재시도 비용 감소 → 안정성 개선**

---

### 회고

---

- 단일 서버 환경에서는 DB 락으로도 충분했겠지만, 추후 **분산 환경을 도입할 가능성이 있기 때문에** Redisson 도입이 필요.
- Redis Cache를 사용할 예정이므로 Redisson을 사용하는 것이 타당해보임
- TPS 자체를 늘리기보다는, **데이터 정합성과 안정성 확보**에 초점을 맞춘 성능 개선으로 이해 가능


</details>


<details>
<summary> 🖥️ 부하/모니터링 - 컨슈머 스케일링을 통한 성능개선 </summary>

### 현재 우리의 상황

- 우리 서비스의 목표 사용자들은 판교(성남)의 개발자들
- 2024-12-29일 기준 총 임직원수 79000명
- <a href="https://m.news.zum.com/articles/95421009/%ED%8C%90%EA%B5%90%ED%85%8C%ED%81%AC%EB%85%B8%EB%B0%B8%EB%A6%AC-%EC%9E%85%EC%A3%BC-%EA%B8%B0%EC%97%85-11-%EC%A6%9D%EA%B0%80-2030-%EC%9E%84%EC%A7%81%EC%9B%90%EC%9D%B4-%EC%A3%BC%EB%A5%98?utm_source=chatgpt.com">판교테크노밸리 입주 기업 11% 증가... 2030 임직원이 주류</a>

---

### 1. 모수 파악

- **판교 테크노밸리 전체 임직원:** 약 **7.9만 명**
- IT·개발 직군 비중을 보수적으로 **50%**만 잡아도 → 약 **3.9~4만 명** 개발자
- 스타트업·대기업 근무지 특성상 **20~40대 비율**이 매우 높고, IT 밋업/스터디 문화에 친숙한 인구가 많음

---

### 2. 서비스 특성과 잠재 수요

- 비슷한 로컬 기반 모임/네트워킹 앱(예: 소모임, Meetup, OKKY 번개) 데이터를 보면:
    - **회원가입 전환율**: 해당 타겟군 중 **10~15%** 정도가 앱에 회원가입
    - **활성 사용자(Active User)**: 가입자의 **30~40%** 정도가 월 1회 이상 접속

    <aside>

    ```yaml
    모수: 40,000명 (판교 개발자)
    회원가입: 10% → 4,000명
    월간 활성 사용자(MAU): 35% → 약 1,400명
    동시접속자(이벤트 피크): MAU의 5~8% → 약 70~110명
    ```

    </aside>


---

### 3. 정리

- 잘 됐다고 가정한 초기 단계: **MAU 약 1,000~2,000명 (MAU = Mothly Active User)**
- 피크 동시접속: **100명 내외**
- 장기적으로 판교+성남 전역 확장 시 **MAU 수만 명 가능**
- 이 수치는 Kafka·MSA 전환 타이밍을 잡는 기준선으로 활용 가능

---

### 4. 부하 테스트 및 모니터링

![테스트시나리오.png](docs/image/테스트시나리오.png)

### 실험 개요 (Introduction)

- **목적**: Kafka 파티션 및 컨슈머 수 변화에 따른 메시지 처리 성능(처리량, 지연)의 차이를 검증
- **환경**: 동일한 토픽(21개), 메시지 부하 조건은 동일
- **변수**: 파티션(1, 3, 12), 컨슈머 수(1, 3, 12) 외에도 다양한 변수를 주면서 테스트 진행
- 파티션1개, 컨슈머 1개

  ![파티션1,컨수머1.png](docs/image/파티션1,컨수머1.png)

    - 메시지가 들어오지만 처리량이 한계에 도달 → **Lag가 선형적으로 증가**

---

- 파티션 3개, 컨슈머 3개

  ![파티션3,컨수머3.png](docs/image/파티션3,컨수머3.png)

    - 처리량이 증가하며 Lag이 **0 근처로 유지**

---

- 파티션 12개, 컨슈머 12개

  ![파티션12,컨수머12.png](docs/image/파티션12,컨수머12.png)
  
    - 소비 속도가 생산 속도와 거의 동일

---

→ 최종 파티션/컨슈머 수 결정에 테스트 결과 반영

</details>


<details>
<summary> 📈 Elasticsearch 를 통한 조회 성능 개선 </summary>

### 1. 배경

우리 서비스는 게시글 조회 시 다양한 조건 검색과 페이징 처리가 빈번하게 발생한다.

단순 조회나 소규모 데이터셋의 경우 MySQL만으로도 충분히 대응 가능하지만 데이터가 수십만 건 이상 축적되고 검색 조건이 복잡해질수록 MySQL 기반 페이징 성능의 한계가 두드러진다.

이에 따라 대규모 데이터 처리와 고성능 검색에 특화된 Elasticsearch(이하 ES)를 도입하였으며 실제 성능 차이를 수치적으로 검증하기 위해 벤치마크 테스트를 수행하였다.

---

### 2. 테스트 방법

- **데이터 세팅**
    - 게시글 100,000건 생성 후, MySQL과 ES에 동일하게 적재
    - 랜덤 조건: 성별(Gender), 최대 인원(maxParticipant) 등
- **테스트 환경**
    - MySQL (InnoDB, 기본 인덱스 세팅)
    - Elasticsearch 8.x
    - Spring Boot 환경에서 JUnit 기반 벤치마크 코드 작성
- **측정 방식**
    - `System.currentTimeMillis()` 기반으로 쿼리 수행 시간을 측정
    - 각 구간별 평균값 산출
- **벤치마크 시나리오**
    >    1. 얕은 페이지 조회 (0~50 페이지)
    >    2. 중간 페이지 조회 (100~110 페이지)
    >    3. 깊은 페이지 조회 (500~510 페이지)
    >    4. 조건 랜덤 조회 (20회 반복)

---

### 3. 테스트 코드 (요약)

```java
@Test
@DisplayName("성능 벤치마크 (평균값 출력)")
void benchmark() {
    double[] shallow = runBenchmark("0 ~ 50 페이지", 0, 50, false);
    double[] deep100 = runBenchmark("100 ~ 110 페이지", 100, 110, false);
    double[] deep500 = runBenchmark("500 ~ 510 페이지", 500, 510, false);
    double[] random = runBenchmark("조건 랜덤", 10, 20, true);

    System.out.println("\n===== 성능 결과 요약 (평균 ms) =====");
    System.out.printf("0 ~ 50 페이지: MySQL=%.2f ms, ES=%.2f ms%n", shallow[0], shallow[1]);
    System.out.printf("100 ~ 110 페이지: MySQL=%.2f ms, ES=%.2f ms%n", deep100[0], deep100[1]);
    System.out.printf("500 ~ 510 페이지: MySQL=%.2f ms, ES=%.2f ms%n", deep500[0], deep500[1]);
    System.out.printf("조건 랜덤(20회): MySQL=%.2f ms, ES=%.2f ms%n", random[0], random[1]);
}
```

---

### 4. 벤치마크 결과 요약 (MySQL vs Elasticsearch 평균 응답 시간)

| 구간            | MySQL (평균) | Elasticsearch (평균) |
|---------------|------------|--------------------|
| 0 ~ 50 페이지    | 30.29 ms   | 12.25 ms           |
| 100 ~ 110 페이지 | 16.36 ms   | 5.82 ms            |
| 500 ~ 510 페이지 | 15.45 ms   | 6.73 ms            |
| 조건 랜덤(20회)    | 44.27 ms   | 6.82 ms            |

![벤치마크결과.png](docs/image/%EB%B2%A4%EC%B9%98%EB%A7%88%ED%81%AC%EA%B2%B0%EA%B3%BC.png)
---

### 5. 테스트 분석 및 결론

- **얕은 페이지(0~50)**: MySQL도 크게 뒤지지 않지만, ES가 평균적으로 2~3배 빠르다.
- **깊은 페이지(500+)**: MySQL 성능이 눈에 띄게 떨어지지는 않았지만 ES가 훨씬 안정적이다.
    - MySQL은 OFFSET 기반 페이징이라 페이지가 깊어질수록 `LIMIT` 전에 불필요한 데이터를 스캔한다.
    - ES는 검색엔진 특화 구조라 페이징 시에도 빠른 응답속도를 보인다.
- **랜덤 조건 조회**: MySQL은 조인/조건 조합이 많아질수록 느려지지만, ES는 거의 일정한 속도를 유지한다.
- **종합**: 단순 조회만 하면 MySQL도 충분하지만, 다양한 조건 검색 + 깊은 페이징 환경에서는 ES의 장점이 크게 드러난다.

</details>
