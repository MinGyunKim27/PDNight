import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '10s',
};

// 테스트 설정
const BASE_URL = 'http://localhost:8080'; // 실제 서버 주소로 변경
const NUM_USERS = 10;

export function setup() {
    const tokens = [];

    for (let i = 0; i < NUM_USERS; i++) {
        const username = `user${i}`;
        const password = 'test1234';

        // 회원가입
        const signupRes = http.post(`${BASE_URL}/api/auth/signup`, JSON.stringify({
            username,
            password,
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        check(signupRes, {
            'signup success or already exists': (res) =>
                res.status === 201 || res.status === 400, // 이미 가입된 경우도 통과시킴
        });

        // 로그인
        const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
            username,
            password,
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        check(loginRes, {
            'login success': (res) => res.status === 200,
        });

        const token = JSON.parse(loginRes.body).token;
        tokens.push(token);
    }

    return { tokens };
}

export default function (data) {
    const token = data.tokens[Math.floor(Math.random() * data.tokens.length)];

    const res = http.post(`${BASE_URL}/api/events/1/participants`, null, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    check(res, {
        'participants success or fail': (r) => r.status === 200 || r.status === 400 || r.status === 409,
    });

    sleep(1);
}
