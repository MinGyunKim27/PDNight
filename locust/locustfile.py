from locust import HttpUser, TaskSet, task, between, events, User
import json
import random
from locust.runners import STATE_STOPPING, STATE_STOPPED, STATE_CLEANUP
from locust.stats import stats_printer

class SignupUser(HttpUser):
    wait_time = between(0.1, 0.3)  # 사용자 요청 간 대기 시간

    user_count = 0  # 사용자 수를 추적하기 위한 클래스 변수

    @task
    def signup(self):
        SignupUser.user_count += 1
        user_id = SignupUser.user_count

        email = f"user{user_id}@naver.com"

        payload = {
            "name": f"user{user_id}",
            "password": "Test@1234",
            "email": email,
            "hobbyIdList": [],
            "techStackIdList": [],
            "nickname": "nickname",
            "gender": "MALE",
            "age": "30",
            "jobCategory": "BACK_END_DEVELOPER",
            "region": "PANGYO_DONG",
            "workLocation": "PANGYO_DONG",
            "comment": "안녕하세요! 5년차 백엔드 개발자입니다. Spring Boot와 마이크로서비스 아키텍처에 깊은 관심이 있으며, 클린코드와 TDD를 지향합니다."
        }

        headers = {'Content-Type': 'application/json'}

        with self.client.post("/api/auth/signup", data=json.dumps(payload), headers=headers, catch_response=True) as response:
            if response.status_code == 201:
                response.success()
                print(f"✅ 회원가입 성공: {email}")
            elif response.status_code == 409:
                response.failure("❌ 이미 존재하는 사용자")
                print(f"❌ 이미 존재하는 사용자: {email}")
            elif response.status_code >= 500:
                response.failure("❌ 서버 오류")
                print(f"❌ 서버 오류: {email}")
            else:
                response.failure(f"❌ 회원가입 실패: {response.status_code}")
                print(f"❌ 회원가입 실패: {email}, 상태코드: {response.status_code}")


# 커스텀 카운터
success_count = 0
conflict_count = 0
fail_count = 0

# class EventTestUser(HttpUser):
#     wait_time = between(0.5, 1)  # 유저 간 요청 간격
#     base_url = "http://localhost"
#
#     # 유저 ID와 토큰 저장
#     users = []
#
#     @events.test_start.add_listener
#     def on_test_start(environment, **kwargs):
#         EventTestUser.users = []
#         for i in range(1, 201):
#             email = f"user{i}@naver.com"
#             password = "Test@1234"
#             with environment.runner.clients[0].post(
#                 "/api/auth/login",
#                 json={"email": email, "password": password},
#                 headers={"Content-Type": "application/json"},
#                 catch_response=True
#             ) as res:
#                 if res.status_code == 200:
#                     try:
#                         token = res.json()["data"]["token"]
#                         EventTestUser.users.append({"token": token, "id": i})
#                     except Exception as e:
#                         print(f"JSON parsing failed for user{i}: {e}")
#
#     def on_start(self):
#         if not self.users:
#             self.setup()  # 유저 미리 로딩
#         index = (self.environment.runner.user_count - 1) % len(self.users)
#         self.user = self.users[index]
#
#     @task
#     def participate_event(self):
#         global success_count, conflict_count, fail_count
#
#         headers = {
#             "Content-Type": "application/json",
#             "Authorization": f"Bearer {self.user['token']}"
#         }
#
#         payload = {
#             "userId": self.user["id"],
#             "lectureId": 1
#         }
#
#         with self.client.post("/api/events/1/participants",
#                               json=payload,
#                               headers=headers,
#                               catch_response=True) as res:
#             if res.status_code == 200:
#                 success_count += 1
#                 res.success()
#             elif res.status_code == 409:
#                 conflict_count += 1
#                 res.success()
#             else:
#                 fail_count += 1
#                 res.failure(f"Unexpected status {res.status_code}: {res.text}")


# 테스트 종료 시 결과 출력
@events.quitting.add_listener
def _(environment, **kwargs):
    print("\n=== Test Summary ===")
    print(f"Success: {success_count}")
    print(f"Conflict: {conflict_count}")
    print(f"Fail: {fail_count}")