# 🌃 판교의 밤 (PDNight)
개발자들의 오프라인 번개모임 플랫폼

---
## 프로젝트 소개
>**판교의 밤**은 IT 종사자들이 번개모임을 만들고, 참여하며, 신뢰를 쌓아가는 오프라인 커뮤니티 플랫폼입니다.
> 근무지역/거주지역, 직군, 취미/기술스택 등의 공통 관심사를 기반으로 한 모임을 추천하고 연결합니다.

---
## 기술 스택
### Backend
- Language
  - Java 17

- Framework
  - Spring Boot
  - Spring Security
  - Spring Data JPA

- Library
  - QueryDSL
  - Redisson

---
### Database
- MySQL
- Redis
---
### Infra
- AWS EC2
- AWS RDS
- Nginx

---
### CI/CD
- Github Actions
- Docker

---
## ERD

<details>
  <summary> 판교의 밤 ERD </summary>

![Alchol(중독자) v2.png](../../../../../../Downloads/Alchol%28%EC%A4%91%EB%8F%85%EC%9E%90%29%20v2.png)

</details>

---
## API

<details>
  <summary> 전체 API 명세 </summary>

### 인증 - Auth API

| 기능           | 메소드      | 엔드포인트                      | 권한      | 설명                  |
|--------------|----------|----------------------------|---------|---------------------|
| 회원가입         | `POST`   | `/api/auth/signup`         | PUBLIC  | 새로운 사용자 회원가입        |
| 로그인          | `POST`   | `/api/auth/login`          | PUBLIC  | 사용자 로그인, 토큰 발급      |
| 로그아웃         | `POST`   | `/api/auth/logout`         | USER    | 사용자 로그아웃            |
| 회원탈퇴         | `DELETE` | `/api/auth/withdraw`       | USER    | 사용자 계정 비활성화         |

---
### 사용자 - User API

| 기능               | 메소드  | 엔드포인트                          | 권한   | 설명                     |
| ---------------- | ---- | ------------------------------ | ---- | ---------------------- |
| 내 좋아요 게시글 조회     | GET  | `/api/users/my/likedPosts`     | User | 내가 좋아요한 게시글 목록 조회      |
| 신청/성사된 게시글 목록 조회 | GET  | `/api/users/my/confirmedPosts` | User | 내가 신청했거나 성사된 게시글 목록 조회 |
| 내가 작성한 게시글 조회    | GET  | `/api/users/my/writtenPosts`   | User | 내가 직접 작성한 게시글 목록 조회    |
| 내 프로필 조회  | GET   | `/api/users/my/profile`      | User | 내 프로필 정보 조회    |
| 프로필 수정    | PATCH | `/api/users/my/profile`      | User | 내 프로필 정보 수정    |
| 비밀번호 수정   | PATCH | `/api/users/my/password`     | User | 비밀번호 변경        |
| 상대 프로필 조회 | GET   | `/api/users/{Id}/profile`    | User | 다른 사용자의 프로필 조회 |
| 사용자 평가 조회 | GET   | `/api/users/{Id}/evaluation` | User | 특정 사용자의 평가 조회  |
| 사용자 리뷰 조회           | `GET`    | `/api/users/{userId}/review`                | USER               | 특정 사용자의 받은 리뷰 조회          |
| 내 받은 리뷰 조회          | `GET`    | `/api/users/my/review`                      | USER            | 내가 받은 리뷰 목록 조회            |
| 내가 쓴 리뷰 조회          | `GET`    | `/api/users/my/writtenReview`               | USER            | 내가 작성한 리뷰 목록 조회           |
| 인물 검색 조회 기능 추가      | `GET`    | `/api/users/search`                         | USER            | 사용자(인물) 검색 기능             |
| 내 초대받은 목록 조회        | `GET`    | `/api/users/my/invited`                     | USER            | 내가 받은 초대 목록 조회            |
| 내가 보낸 초대 목록 조회      | `GET`    | `/api/users/my/invite`                      | USER            | 내가 보낸 초대 목록 조회            |
| 팔로우 추가              | `POST`   | `/api/users/{userId}/follow`                | USER          | 특정 사용자 팔로우 추가             |
| 팔로우 삭제              | `DELETE` | `/api/users/{userId}/follow`                | USER         | 특정 사용자 팔로우 취소             |
| 팔로잉 목록 조회           | `GET`    | `/api/users/my/following`                   | USER          | 내가 팔로우한 사용자 목록 조회         |

---
### 취미/기술스택  - Hobby,TechStack API

| 기능        | 메소드   | 엔드포인트                        | 권한   | 설명             |
| --------- | ----- | ---------------------------- | ---- | -------------- |
| 취미 추가            | POST | `/api/hobbies`                 | User | 나의 취미 항목 추가            |
| 취미 리스트 조회        | GET  | `/api/hobbies`                 | User | 전체 취미 항목 목록 조회         |
| 기술 스택 추가         | POST | `/api/techStacks`              | User | 나의 기술 스택 추가            |
| 기술 스택 리스트 조회     | GET  | `/api/techStacks`              | User | 전체 기술 스택 목록 조회         |

---
### 게시글 - Post API

| 기능        | 메소드    | 엔드포인트                                            | 권한             | 설명                |
| --------- | ------ | ------------------------------------------------ |----------------| ----------------- |
| 게시글 참여 신청 | POST   | `/api/posts/{postId}/participate`                | USER | 해당 게시글에 참여 신청     |
| 게시글 참여 취소 | DELETE | `/api/posts/{postId}/participate`                | USER               | 내가 신청한 참여를 취소     |
| 신청자 수락/거절 | PATCH  | `/api/posts/{postId}/participate/users/{userId}` | USER           | 신청자의 참여를 수락 또는 거절 |
| 신청자 목록 조회 | GET    | `/api/posts/{postId}/participate`                | USER           | 해당 게시글의 신청자 목록 조회 |
| 참여자 목록 조회 | GET    | `/api/posts/{postId}/participate/confirmed`      | USER           | 확정된 참여자 목록 조회     |
| 게시글 목록 조회 (신청자 수 포함) | `GET`    | `/api/posts`                                | USER            | 신청자 수, 확정 참여자 수 포함 게시글 조회 |
| 게시글 참여 신청 (제한 로직 추가) | `POST`   | `/api/posts/{postId}/participate`           | USER | 제한 조건이 적용된 게시글 참여 신청      |
| 게시글 초대              | `POST`   | `/api/posts/{postId}/users/{userId}/invite` | USER            | 특정 유저를 게시글에 초대            |
| 추천 게시글 목록 조회        | `GET`    | `/api/posts/suggestedPosts`                 | USER            | 사용자에게 추천되는 게시글 목록 조회      |
| 초대 취소               | `DELETE` | `/api/posts/{postId}/users/{userId}/invite` | USER            | 게시글 초대를 취소함               |
| 게시글 좋아요    | POST   | `/api/posts/{id}/likes`                            | USER   | 게시글에 좋아요를 누름    |
| 게시글 좋아요 취소 | DELETE | `/api/posts/{id}/likes`                            | USER   | 게시글의 좋아요를 취소함   |
| 사용자 리뷰     | POST   | `/api/posts/{postId}/participants/{userId}/review` | USER | 참여한 사용자에게 리뷰 작성 |
| 게시글 좋아요    | POST   | `/api/posts/{id}/likes`                            | USER   | 게시글에 좋아요를 누름    |
| 게시글 좋아요 취소 | DELETE | `/api/posts/{id}/likes`                            | USER   | 게시글의 좋아요를 취소함   |
| 사용자 리뷰     | POST   | `/api/posts/{postId}/participants/{userId}/review` | USER | 참여한 사용자에게 리뷰 작성 |

---
### 댓글 - Comment API

| 기능           | 메소드    | 엔드포인트                                        | 권한    | 설명               |
| ------------ | ------ | -------------------------------------------- |-------| ---------------- |
| [관리자] 댓글 삭제 | `DELETE` | `/api/admin/posts/{postId}/comments/{id}`    | ADMIN | 관리자가 댓글을 삭제함     |
| 댓글 추가        | `POST`   | `/api/posts/{postId}/comments`               | USER | 게시글에 댓글 추가       |
| 댓글 삭제        | `PATCH`  | `/api/posts/{postId}/comments/{id}`          | USER| 댓글을 논리적으로 삭제     |
| 댓글 수정        | `DELETE` | `/api/posts/{postId}/comments/{id}`          | USER | 댓글을 물리적으로 삭제     |
| 댓글 다건 조회     | `GET`    | `/api/posts/{postId}/comments`               | USER | 해당 게시글의 댓글 목록 조회 |
| 대댓글 생성  | `POST`   | `/api/posts/{postId}/comments/{id}/comments` |USER| 특정 댓글에 대댓글 추가    |

---
### 이벤트 - Event API

| 기능                  | 메소드    | 엔드포인트                                       | 권한              | 설명                        |
| ------------------- | ------ | ------------------------------------------- |-----------------| ------------------------- |
| [관리자] 게시물 삭제        | `DELETE` | `/api/admin/posts/{id}`                     | ADMIN           | 관리자가 게시글을 삭제함             |
|[관리자] 이벤트 추가    | POST   | `/api/admin/events`                   | ADMIN | 관리자가 이벤트 등록          |
|[관리자] 이벤트 조회    | GET    | `/api/admin/events/{id}`              | ADMIN | 특정 이벤트 상세 조회         |
|[관리자] 이벤트 수정    | PATCH  | `/api/admin/events/{id}`              | ADMIN | 이벤트 정보 수정            |
|[관리자] 이벤트 삭제    | DELETE | `/api/admin/events/{id}`              | ADMIN | 이벤트 삭제               |
| [관리자] 이벤트 참가자 조회 | GET    | `/api/admin/events/{id}/participants` | ADMIN | 이벤트 참가자 목록 조회        |
|[관리자] 이벤트 전체 조회 | GET    | (미정)                                  | ADMIN      | 등록된 이벤트 전체 목록 조회     |
| 이벤트 조회           | GET    | (미정)                                  | USER | 일반 유저가 볼 수 있는 이벤트 조회 |
| 이벤트 참가           | POST   | `/api/events/{id}/participants`       | USER | 일반 사용자의 이벤트 참가       |

---
### 쿠폰 - Coupon API

| 기능            | 메소드    | 엔드포인트                     | 권한    | 설명                |
| ------------- | ------ | ------------------------- |-------| ----------------- |
| [관리자] 강제 회원 탈퇴 | `DELETE` | `/api/admin/users/{id}`   | ADMIN | 관리자에 의한 회원 강제 탈퇴  |
| [관리자] 닉네임 강제 변경 | `PATCH`  | `/api/admin/users/{id}`   | ADMIN | 회원 닉네임 강제 변경      |
| [관리자] 전체 유저 조회 | `GET`    | `/api/admin/users`        | ADMIN | 전체 유저 리스트 조회      |
| [관리자] 쿠폰 등록  | `POST`   | `/api/admin/coupons`      | ADMIN | 쿠폰 생성             |
|[관리자] 쿠폰 조회   | `GET`    | `/api/admin/coupons/{id}` | ADMIN | 쿠폰 상세 조회          |
| [관리자] 쿠폰 수정   | `PATCH`  | `/api/admin/coupons/{id}` | ADMIN | 쿠폰 정보 수정          |
| [관리자] 쿠폰 삭제   | `DELETE` | `/api/admin/coupons/{id}` | ADMIN | 쿠폰 삭제             |
| 쿠폰 사용         | `PATCH`  | `/api/coupons/{id}`       | USER  | 쿠폰 사용 처리          |
| 보유한 쿠폰 조회     | `GET`    | `/api/users/my/coupons`   | USER  | 로그인 사용자의 쿠폰 목록 조회 |

</details>

---
## 핵심 기능

<details>
  <summary> 주요 기능 소개 </summary>

### User - 사용자

- **추천 모임 제공**
  - 위치(근무지/거주지), 연령대, 직군/취미/기술스택 기반으로 모임 추천

- **프로필 열람**
  - 타 유저의 프로필을 확인하여 신뢰도 및 공통 관심사 파악

- **신뢰도 평가**
  - 모임 종료 후 참여자 간 별점 및 코멘트 평가 기능

- **팔로우 기능**
  - 팔로우한 유저를 모임 생성 시 초대 가능

---

### Post - 모임

- **모임 필터 검색**
  - 연령대, 직군, 취미/기술 등 관심사 기반 조건으로 필터링 검색 가능

- **좋아요 기능**
  - 모임을 찜해두고 추적 가능
  - 좋아요 수가 많은 모임은 상단에 우선 노출

- **댓글 기능**
  - 모임에 대한 질문 및 커뮤니케이션 지원
  - 대댓글 기능으로 추가 코멘트 가능 (단, 1단계만 허용)

- **참여 신청 기능**
  - 신청자 수 제한 기반 신청 및 수락/거절 기능
  - 선착순 모임은 신청 순서대로 자동 승인
  - Redisson Lock 기반 동시성 제어 적용

- **모임 상태 관리**
  - 모집 완료 시 `성사`, 종료된 모임은 `닫힘` 상태로 전환
  - `닫힘` 상태 모임은 기록용으로 조회 가능

---

### Chat - 채팅

- **모임 채팅방 제공**
  - 모임 성사 시 자동 채팅방 생성
  - 참여자 간 실시간 커뮤니케이션 가능
  - 모임 종료 후에도 채팅방 유지

- **1:1 또는 그룹 채팅 기능**
  - 사용자가 독립적으로 채팅방 생성 가능
  - 참여자 설정을 통해 접근 제어 가능

---

### Event - 이벤트

- **이벤트 기반 모임 기능**
  - 관리자가 특정 조건의 이벤트 모임 생성 가능 (예: 유명인 초청 번개)

---

### Coupon - 쿠폰

- **쿠폰 지급 기능**
  - 관리자가 특정 유저에게 모임 지원금 형태의 쿠폰 제공 가능

</details>

---
## 기술 선정 이유

<details>

<summary> 언어선택 : Java </summary>

`Spring Boo`t는 `Kotlin` 과의 궁합도 좋아서 Kotlin을 사용하는 경우도 많지만 우리는 Java를 선택했습니다.
그 이유는 다음과 같습니다.
- 팀원 전체가 Java에 익숙하여 개발 속도가 빠르다.
  - Kotlin은 학습한 경험은 있으나 실무 수준의 숙련도가 부족해 초기 생산성이 떨어질 수 있다고 판단
  - 문법이 명확하고 팀 내에서 오랫동안 사용해온 만큼 코드 스타일의 일관성을 쉽게 유지할 수 있다.

- 안정적인 생태계와 광범위한 레퍼런스
  - Spring Boot 와의 호환성도 뛰어나고 문제가 발생했을 때 해결 방법이나 사례가 풍부하여 개발 및 유지보수에 유리합니다.

결론적으로 학습 비용과 개발 효율성, 유지보수성을 고려했을 때 우리 팀에게는 Java가 더 적합하다고 판단하였습니다.

</details>

---
<details>

<summary> CI/CD : Github Actions </summary>

우리 팀은 AWS CodePipeline과 GitHub Actions를 비교한 끝에 GitHub Actions를 CI/CD 도구로 채택하였습니다.
그 이유는 아래와 같습니다:

1. 학습 및 사용 편의성
- GitHub 저장소 내에서 바로 CI/CD 설정 가능 → 진입 장벽이 낮고 직관적
- 템플릿, 공식 문서, 커뮤니티 자료가 풍부하여 학습 비용이 낮음
- 설정 파일인 .yml 문법이 간결하고 명확함 → 기존 개발자들에게 익숙하고 유지보수가 쉬움
- AWS CodePipeline은 IAM 권한, 연결 리소스 설정 등 복잡한 절차가 필요

---
2. 비용 측면에서의 이점
- GitHub Actions
  - Public Repository: 완전 무료
  - Private Repository: 월 2,000분 무료 (기본 제공)

- CodePipeline
  - 파이프라인 당 월 1달러
  - 작업 시간 분당 과금 (100분 무료)
  - 실제 사용 중 과금 이슈가 있었던 사례 존재

---
3. 속도 및 성능
- Gradle 캐시 활용이 가능해 빌드 속도 향상
- 병렬 처리 및 빠른 피드백 루프 구성 가능
- 실제 빌드/배포 속도에서도 CodePipeline 보다 빠르다는 사례 다수 존재

---
4. 확장성과 유연성
- 다양한 서드파티 액션과 통합 가능
- AWS 외에도 GCP, Azure 등 다양한 클라우드와 연동 가능
- Kubernetes와 같은 컨테이너 플랫폼 확장 시에도 유리

---
5. 관리 및 통합 편의성
- GitHub 기반 개발 워크플로우 내에서 모든 CI/CD 관리 가능
- 코드, PR, 테스트, 배포까지 한 곳에서 추적 → 중앙 집중형 관리
- 에러 발생 시 GitHub 내 로그 확인이 쉬움

---
### 결론
우리 팀은 다음과 같은 판단 아래 GitHub Actions를 채택하였습니다.
- 개발 진입 장벽이 낮다.
- 문서 및 사례가 풍부하여 다양한 방향으로 설계가 가능하다.
- 비용 예측면에서 용이하다.
- 설정 및 유지보수가 간단하고 접근성이 뛰어나다.

---

</details>

---
<details>

<summary> Nginx 도입 </summary>

우리 프로젝트에는 외부 요청 처리와 보안을 강화하기 위해 Nginx를 함께 구성했습니다.
Nginx 를 도입하게 된 상세한 이유는 다음과 같습니다.

1. 리버스 프록시 역할
- 클라이언트 요청(HTTPS)을 받아 내부 애플리케이션 서버(HTTP)로 전달
> https://도메인/api/... → Nginx(443) → http://localhost:8080
- Spring 서버를 직접 외부에 노출하지 않고 보안 계층을 하나 더 쌓음

---
2. HTTPS / SSL 처리
- SSL 인증서를 Nginx에서 적용하여 HTTPS 통신 처리
- Spring Boot를 단독으로 사용할 경우 HTTPS 통신을 위한 복잡한 설정이 필요
  - Nginx 사용 시 인증서 사용을 위한 설정이 간략
- Let’s Encrypt + certbot 조합으로 무료 SSL 적용 및 자동 갱신 가능

---
3. 로드 밸런싱 (확장성 확보)
- 서버를 여러 개로 늘릴 경우 Nginx가 자동으로 트래픽을 분산
- 현재는 단일 서버지만 추후 서버 확장을 고려해 유연한 구조 확보

---
4. 보안 및 방화벽 기능
- 특정 IP 차단, 경로 제한 등 다양한 접근 제어 설정 가능
- 외부에서는 80/443 포트만 열어두고 내부 서버 포트(8080 등)는 숨김
- DDOS 방어, HTTPS 강제 리디렉션 등도 적용 가능

---
5. 정적 파일 처리
- HTML, 이미지, JS, CSS 등 정적 파일을 빠르게 처리
- 프론트엔드가 분리된 구조라면 향후 유용하게 활용 가능
  - 현재는 백엔드 중심 서비스라 직접적인 체감은 적음

</details>

---
<details>

<summary> Redis를 캐시 라이브러리로 선택한 이유 </summary>

우리 프로젝트는 캐시 시스템 도입 시 다양한 옵션을 검토한 끝에 `Redis`를 최종 선택하였습니다.
선택 이유는 다음과 같습니다.

1. 다양한 자료 구조 지원
- Redis는 단순 key-value 외에도 List, Set, Sorted Set, Hash, Bitmap 등 다양한 자료구조를 지원
- 복잡한 캐시 로직(예: 랭킹, TTL 기반 토큰 저장, 중복 검사 등)을 효율적으로 처리할 수 있음

---
2. 빠르고 안정적인 성능
- 인메모리 기반 구조로 응답 속도가 매우 빠름
- 트래픽이 많아질수록 캐시 히트율을 높여 전체 시스템 부하를 감소시켜줌

---
3. 운영 기능 및 클러스터링
- TTL(만료 시간), Pub/Sub, HyperLogLog 등 다양한 고급 기능 지원
- 클러스터 모드, Sentinel 등 운영 편의 기능이 잘 마련되어 있음

---
4. 비교 요약

| 항목     | Memcached       | Hazelcast    | Redis         |
| ------ | --------------- | ------------ | ------------- |
| 자료 구조  | key-value 단일 구조 | 일부 복잡한 구조 지원 | 다양한 자료 구조 지원  |
| 언어 지원  | 제한적             | Java 중심      | 거의 모든 언어 지원   |
| 사용 편의성 | 간단하지만 기능 제한     | 설정과 구현 복잡    | Spring과 잘 통합됨 |
| 성능     | 매우 빠름           | 고정적          | 빠르고 유연        |
| 확장성    | 낮음              | 분산 구조 우수     | 클러스터/Sentinel 지원 |


</details>

---