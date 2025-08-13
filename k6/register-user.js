import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 1,         // 동시 사용자 수
    iterations: 200,  // 총 50명 생성
};

export default function () {
    const id = (__VU - 1) * 1 + (__ITER + 1); // VU * 반복횟수 → 고유 ID
    const email = `user${id}@naver.com`;

    const payload = JSON.stringify({
        name: `user${id}`,
        password: "Test@1234",
        email: email,
        hobbies: "독서",
        techStacks: "Java",
        nickname: "nickname",
        gender: "MALE",
        age: "30",
        jobCategory: "BACK_END_DEVELOPER",
        region: "PANGYO_DONG",
        workLocation: "PANGYO_DONG",
        comment: "안녕하세요! 5년차 백엔드 개발자입니다. Spring Boot와 마이크로서비스 아키텍처에 깊은 관심이 있으며, 클린코드와 TDD를 지향합니다."
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const res = http.post('http://localhost:8080/api/auth/signup', payload, params);

    const success = check(res, {
        '회원가입 성공 (201)': (r) => r.status === 201,
    });

    if (!success) {
        console.error(`회원가입 실패`);
        console.error(`- email: ${email}`);
        console.error(`- status: ${res.status}`);
        console.error(`- body: ${res.body}`);

        if (res.status === 409) {
            console.error(`이미 존재하는 사용자`);
        } else if (res.status >= 500) {
            console.error(`서버 오류`);
        }
    } else {
        console.log(`회원가입 성공: ${email}`);
    }

    sleep(0.1);
}