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
  - 
- Architecture 
    - Domain-Driven Design : DDD
      - Port & Adapter Pattern
      - Spring Event 기반 도메인 이벤트 처리
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
  <summary> 인증 - Auth API </summary>

### 인증 - Auth API

| 기능 | Method | Endpoint | 설명 | 권한 |
|------|--------|----------|------|------|
| 회원가입 | `POST` | /api/auth/signup | 서비스 이용을 위한 계정 생성| PUBLIC |
| 로그인 | `POST` | /api/auth/login | 사용자 인증을 통해 액세스 토큰을 발급 | PUBLIC |
| 로그아웃 | `POST` | /api/auth/logout | 로그아웃 | USER |
| 비밀번호 수정 | `PATCH` | /api/auth/password | 비밀번호 수정 | USER |
| 회원탈퇴 | `DELETE` | /api/auth/withdraw | 사용자의 계정을 비활성화하고 서비스 이용을 종료 | USER |

</details>

---
<details>
  <summary> 사용자 - User API </summary>

### 사용자 - User API

| 기능 | Method | Endpoint | 설명            | 권한 |
|------|--------|----------|---------------|------|
| 내 쿠폰 사용 | `POST` | /api/users/my/user-coupons/{id} | 내 쿠폰 사용       | USER |
| 팔로우 추가 | `POST` | /api/users/{userId}/follow | 팔로우 추가        | USER |
| 프로필 수정 | `PATCH` | /api/users/my/profile | 프로필 수정        | USER |
| 언팔로우 | `DELETE` | /api/users/{userId}/follow | 언팔로우          | USER |
| 내 프로필 조회 | `GET` | /api/users/my/profile | 내 프로필 조회      | USER |
| 상대 프로필 조회 | `GET` | /api/users/{Id}/profile | 상대 프로필 조회     | USER |
| 인물 검색 조회 기능 추가 | `GET` | /api/users/search | 인물 검색 조회 기능 추가 | USER |
| 내 받은 리뷰 조회 | `GET` | /api/users/my/review | 내 받은 리뷰 조회    | USER |
| 팔로잉 목록 조회 | `GET` | /api/users/my/following | 팔로잉 목록 조회     | USER |
| 내 쿠폰 조회 | `GET` | /api/users/my/user-coupons | 해당 쿠폰의 상세 정보를 반환합니다. | USER |
| 사용자 평가 조회 | `GET` | /api/users/{id}/evaluation | 사용자 평가 조회     | USER |

</details>

---
<details>
  <summary> 취미/기술스택  - Hobby,TechStack API </summary>

### 취미/기술스택  - Hobby,TechStack API

| 기능        | 메소드   | 엔드포인트                        | 권한   | 설명             |
| --------- | ----- | ---------------------------- | ---- | -------------- |
| 취미 추가            | POST | `/api/hobbies`                 | User | 나의 취미 항목 추가            |
| 취미 리스트 조회        | GET  | `/api/hobbies`                 | User | 전체 취미 항목 목록 조회         |
| 기술 스택 추가         | POST | `/api/techStacks`              | User | 나의 기술 스택 추가            |
| 기술 스택 리스트 조회     | GET  | `/api/techStacks`              | User | 전체 기술 스택 목록 조회         |

</details>

---
<details>
  <summary> 게시글 - Post API </summary>

### 게시글 - Post API

| 기능        | 메소드    | 엔드포인트                                            | 권한           | 설명                |
| --------- | ------ | ------------------------------------------------ |--------------| ----------------- |
| 게시글 참여 신청 | POST   | `/api/posts/{postId}/participate`                | USER | 해당 게시글에 참여 신청     |
| 게시글 참여 취소 | DELETE | `/api/posts/{postId}/participate`                | USER             | 내가 신청한 참여를 취소     |
| 신청자 수락/거절 | PATCH  | `/api/posts/{postId}/participate/users/{userId}` | USER         | 신청자의 참여를 수락 또는 거절 |
| 신청자 목록 조회 | GET    | `/api/posts/{postId}/participate`                | USER         | 해당 게시글의 신청자 목록 조회 |
| 참여자 목록 조회 | GET    | `/api/posts/{postId}/participate/confirmed`      | USER         | 확정된 참여자 목록 조회     |
| 게시글 목록 조회 (신청자 수 포함) | `GET`    | `/api/posts`                                | USER          | 신청자 수, 확정 참여자 수 포함 게시글 조회 |
| 게시글 참여 신청 (제한 로직 추가) | `POST`   | `/api/posts/{postId}/participate`           | USER | 제한 조건이 적용된 게시글 참여 신청      |
| 참여자 목록 조회 | `GET` | /api/posts/{postId}/participate/confirmed | 참여자 목록 조회 | USER |
| 신청자 목록 조회 | `GET` | /api/posts/{postId}/participate | 신청자 목록 조회 | USER |
| 게시글 초대              | `POST`   | `/api/posts/{postId}/users/{userId}/invite` | USER          | 특정 유저를 게시글에 초대            |
| 추천 게시글 목록 조회        | `GET`    | `/api/posts/suggestedPosts`                 | USER          | 사용자에게 추천되는 게시글 목록 조회      |
| 초대 취소               | `DELETE` | `/api/posts/{postId}/users/{userId}/invite` | USER          | 게시글 초대를 취소함               |
| 게시글 좋아요    | POST   | `/api/posts/{id}/likes`                            | USER   | 게시글에 좋아요를 누름    |
| 게시글 좋아요 취소 | DELETE | `/api/posts/{id}/likes`                            | USER   | 게시글의 좋아요를 취소함   |
| 사용자 리뷰     | POST   | `/api/posts/{postId}/participants/{userId}/review` | USER | 참여한 사용자에게 리뷰 작성 |
| 게시글 좋아요    | POST   | `/api/posts/{id}/likes`                            | USER   | 게시글에 좋아요를 누름    |
| 게시글 좋아요 취소 | DELETE | `/api/posts/{id}/likes`                            | USER   | 게시글의 좋아요를 취소함   |
| 사용자 리뷰     | POST   | `/api/posts/{postId}/participants/{userId}/review` | USER | 참여한 사용자에게 리뷰 작성 |

</details>

---
<details>
  <summary> 댓글 - Comment API </summary>

### 댓글 - Comment API

| 기능 | Method | Endpoint | 설명 | 권한 |
|------|--------|----------|------|------|
| 댓글 추가 | `POST` | /api/posts/{postId}/comments | 댓글 추가 | USER |
| 대댓글 생성 | `POST` | /api/posts/{postId}/comments/{id}/comments |  대댓글 생성 | USER |
| 댓글 수정 | `PATCH` | /api/posts/{postId}/comments/{id} | 댓글 수정 | USER |
| 댓글 삭제 | `DELETE` | /api/posts/{postId}/comments/{id} | 특정 게시글에 달린 댓글을 삭제 | USER |
| 댓글 다건 조회 | `GET` | /api/posts/{postId}/comments | 댓글 다건 조회 | USER |

</details>

---
<details>
  <summary> 관리자 - ADMIN API </summary>

### 관리자 - ADMIN API

| 기능 | Method | Endpoint | 설명                | 권한 |
|------|--------|----------|-------------------|------|
| [관리자] 사용자에게 쿠폰 할당 | `POST` | /api/admin/users/coupons | 사용자에게 쿠폰 할당  | ADMIN |
| [관리자] 닉네임 강제 변경 | `PATCH` | /api/admin/users/{id} | 닉네임 강제 변경    | ADMIN |
| [관리자] 강제 회원 탈퇴 | `DELETE` | /api/admin/users/{id} | 강제 회원 탈퇴     | ADMIN |
| [관리자] 전체 유저 조회 | `GET` | /api/admin/users | 전체 유저 조회     | ADMIN |
| [관리자] 게시글 삭제 | `DELETE` | /api/admin/posts/{id} | 게시글 삭제       | ADMIN |
| [관리자] 이벤트 추가 | `POST` | /api/admin/events | 이벤트 추가       | ADMIN |
| [관리자] 이벤트 수정  | `PATCH` | /api/admin/events/{id} | 이벤트 수정       | ADMIN |
| [관리자] 이벤트 삭제 | `DELETE` | /api/admin/events/{id} | 이벤트 삭제       | ADMIN |
| [관리자] 이벤트 참가자 조회 | `GET` | /api/admin/events/{id}/participants | 사용자가 특정 이벤트에 참가 신청을 합니다 | ADMIN |
| [관리자] 쿠폰 생성 | `POST` | /api/admin/coupons | 관리자가 새로운 쿠폰을 등록   | ADMIN |
| [관리자] 쿠폰 수정 | `PATCH` | /api/admin/coupons/{id} | 쿠폰 수정        | ADMIN |
| [관리자] 쿠폰 삭제 | `DELETE` | /api/admin/coupons/{id} | 쿠폰 삭제        | ADMIN |
| [관리자] 쿠폰 조회 | `GET` | /api/admin/coupons/{id} | 해당 쿠폰의 상세 정보를 반환  | ADMIN |
| [관리자] 댓글 삭제 | `DELETE` | /api/admin/posts/{postId}/comments/{id} | 게시글에 달린 댓글을 강제 삭제 | ADMIN |

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

<summary> Spring Boot / Java </summary>

### [ Spring Boot / Java ]
> 본 프로젝트의 백엔드 프레임워크 및 주요 개발 언어로 사용

---

### 1. 기술 설명

#### Spring Boot
- Spring의 복잡한 설정을 자동화하여 프로젝트 생성, 설정, 실행을 빠르게 처리할 수 있도록 도와주는 확장 프레임워크
- 내장 톰캣, 자동 설정, 스타터 의존성 등을 통해 개발 생산성과 접근성 향상

---

#### Java
- 대표적인 객체지향 언어로 안정적인 JVM 환경과 풍부한 생태계 및 레퍼런스를 갖춘 백엔드 언어
- Spring Boot 와의 호환성도 뛰어나고 다양한 라이브러리 및 툴(IDE, 디버깅 등)이 잘 갖춰져 있어 대규모 서비스 개발과 유지보수에 적합

---

### 2. 기술 장점

#### Spring Boot
1. 자동 설정 및 스타터 의존성 제공 → 직접 설정할 필요 최소화
2. 비즈니스 로직 구현에 집중 가능 → 팀 전체 개발 생산성 향상
3. 내장 웹 서버, Actuator, DevTools 등 실무에서 유용한 기능 내장

---

#### Java
1. 낮은 학습 비용과 높은 생산성
   - 팀원 전체가 익숙 → 빠른 개발 진입, 유지보수 용이
   - 코드 리뷰/협업/디버깅 효율적

---

2. 안정적이고 검증된 생태계
   - JVM 기반 운영 경험과 라이브러리, 툴 풍부
   - 대규모 서비스에 강한 안정성, 성능, 운영성

---

3. Spring Boot와의 궁합
   - Kotlin도 가능하지만, Java는 원래 Spring 생태계의 중심
   - 공식 문서, 레퍼런스, 예제가 풍부해 문제 해결이 쉬움

---

4. 협업 및 일관성 유지에 유리
   - 명확한 타입 시스템과 구조화된 문법
   - 코드 스타일 일관성 확보

---

### 3. 단점 / 주의사항

#### Spring Boot
- 직접 구성 가능한 Spring에 비해 자동화된 만큼 세밀한 설정 제어는 어려울 수 있음
- 규모가 커지면 내부 자동 설정 파악이 어렵고, 복잡한 구조에서는 오히려 추상화가 장애 요인이 될 수 있음

---

#### Java
- Kotlin에 비해 반복 코드가 많고 문법이 장황할 수 있음
- 최신 언어나 트렌디한 기능 도입에는 상대적으로 느릴 수 있음

---

### 4. 도입 배경과 선택 이유

#### Spring Boot
- Spring은 유연하지만 설정이 복잡하고 진입 장벽이 높음
- 반면, Spring Boot는 자동 설정 제공으로 개발 환경 구성 부담이 적고 Spring 기반 애플리케이션을 쉽게 구축할 수 있어 도입

#### Java
- Kotlin도 유망하지만, 우리 팀은 다음 이유로 Java 선택
  - 팀원 전원이 Java에 익숙하여 개발 속도와 협업 효율이 높음
  - Kotlin은 경험은 있으나 실무 수준 숙련도 부족 → 초기 생산성 우려
  - Java는 명확한 문법과 코드 스타일 통일이 쉬워 협업에 유리
  - 레퍼런스가 풍부하고 문제 해결 사례가 많아 유지보수 용이
  - Gradle, JPA, Redis 등 현재 사용하는 기술 스택과의 궁합이 검증됨

</details>

---
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

---
<details>

<summary> 단계적 배포 방식 선택 이유 (SSH → AWS CodeDeploy) </summary>

### [ SSH → AWS CodeDeploy ]
>프로젝트 배포의 단계적 확장

---

### 1. 기술 설명

- SSH (Secure Shell)  
  원격 서버에 직접 접속하여 명령 실행 및 파일 전송을 수행하는 방식의 배포 방법

- AWS CodeDeploy  
  AWS가 제공하는 배포 자동화 서비스로, EC2, Lambda, 온프레미스 환경 등에서  
  무중단 배포, 롤백, 상태 추적 등의 고급 기능을 지원

---

### 2. 기술 장점

#### SSH
- 유연성 높음 : 스크립트와 커맨드를 자유롭게 구성 가능
- 설정 단순 : 별도 서비스 없이 바로 사용 가능
- 다양한 CI 도구와 연동 쉬움 (예: GitHub Actions)

#### AWS CodeDeploy
- 자동화된 배포 : 수동 개입 없이 일관된 배포 프로세스 구성 가능
- 무중단 배포 가능 : 블루/그린, 롤링 등 전략 지원
- 확장성 우수 : 여러 인스턴스에 동시에 배포 가능
- 상태 추적 및 실패 시 롤백 등 고급 기능 제공

---

### 3. 단점 / 주의사항

#### SSH
- 확장성 낮음 : 서버 수가 늘어나면 반복 작업 많아짐
- 장애 대응 어려움 : 롤백, 상태 추적 등의 기능 없음
- 보안 설정 번거로움 : 키 관리, 접근 통제 등을 직접 구성해야 함

#### AWS CodeDeploy
- AWS 종속성 존재: 멀티 클라우드 대응은 어려움
- 초기 설정 복잡 : appspec.yml, IAM 권한, 인스턴스 태깅 등 구성 필요
- 비용 발생 가능성 : EC2는 무료, 온프레미스는 인스턴스당 0.02 USD 과금

---

### 4. 도입 배경과 필요성

현재 프로젝트는 소규모이고 빠른 배포 피드백이 필요한 초기 단계이므로  
설정이 간단하고 유연하게 사용할 수 있는 SSH 방식을 우선 도입합니다.

이후 트래픽 증가나 인프라 확장이 요구되는 시점에는  
자동화, 무중단 배포, 상태 추적 기능이 강화된 AWS CodeDeploy로 전환하여  
배포 안정성과 운영 효율성을 확보할 계획입니다.

</details>



---
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

---
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

| 항목         | Memcached             | Hazelcast               | Redis                           |
|--------------|------------------------|--------------------------|---------------------------------|
| 자료 구조     | key-value 단일 구조     | 일부 복잡한 구조 지원     | 다양한 자료 구조 지원           |
| 언어 지원     | 제한적                 | Java 중심               | 거의 모든 언어 지원             |
| 사용 편의성   | 간단하나 기능 제한적    | 설정과 구현 복잡          | Spring과의 통합 및 자료 풍부     |
| 성능          | 매우 빠름             | 고정적                   | 빠르고 유연                     |
| 확장성        | 낮음                  | 분산 구조 우수            | 클러스터 / Sentinel 구성 유연함 |

</details>

---
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