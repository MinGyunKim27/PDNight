import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 1,
    iterations: 1,
};

const BASE_URL = 'http://localhost:8080';

const USERS = Array.from({ length: 10 }, (_, i) => ({
    email: `user${i}@test.com`,
    password: 'password123',
    nickname: `User${i}`,
}));

export default function () {
    let tokens = [];

    USERS.forEach(user => {
        // 1. 회원가입 요청
        const signupRes = http.post(`${BASE_URL}/api/auth/signup`, JSON.stringify(user), {
            headers: { 'Content-Type': 'application/json' },
        });

        check(signupRes, {
            'signup success or already exists': (res) => res.status === 201 || res.status === 400,
        });

        // 2. 로그인 요청
        const loginRes = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({
            email: user.email,
            password: user.password,
        }), {
            headers: { 'Content-Type': 'application/json' },
        });

        check(loginRes, {
            'login success': (res) => res.status === 200 && res.json('accessToken') !== undefined,
        });

        if (loginRes.status === 200) {
            const token = loginRes.json('accessToken');
            tokens.push(token);
        }
    });

    console.log(`Collected ${tokens.length} tokens:\n`, tokens);
}
