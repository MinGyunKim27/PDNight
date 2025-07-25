import { check, sleep } from 'k6';
import http from 'k6/http';
import { Counter } from 'k6/metrics';

export let options = {
    vus: 200,
    duration: '1s',
};

// 커스텀 카운터 메트릭
export let success = new Counter('participants_success');
export let conflict = new Counter('participants_conflict');
export let notFound = new Counter('participants_not_found');
export let fail = new Counter('participants_fail');

export function setup() {
    const baseUrl = 'http://localhost:8080';
    const users = [];

    for (let i = 1; i <= 200; i++) {
        const email = `user${i}@naver.com`;
        const password = 'Test@1234';

        // 로그인 요청
        const res = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({ email, password }), {
            headers: { 'Content-Type': 'application/json' },
        });

        let token = null;
        try {
            const json = res.json();
            token = json?.data?.token;
        } catch (e) {
            console.error(`Login JSON parse error for user${i}:`, e);
        }

        if (token) {
            users.push({ token, userId: i });
        } else {
            console.error(`Login failed for user${i}, status: ${res.status}`);
        }
    }

    return users;
}

export default function (users) {
    const baseUrl = 'http://localhost:8080';
    const index = (__VU - 1) % users.length;
    const user = users[index];

    const res = http.post(`${baseUrl}/api/posts/1/participate`, JSON.stringify({
        userId: user.id,
        postId: 1,
    }), {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${user.token}`,
        },
    });

    // 응답 분류에 따른 카운팅
    if (res.status === 200 || res.status === 201) {
        success.add(1);
    } else if (res.status === 409) {
        conflict.add(1);
    } else if (res.status === 404) {
        notFound.add(1);
    } else {
        fail.add(1);
        console.error(`Unexpected status=${res.status}, body=${res.body}`);
    }

    check(res, {
        '응답 코드가 201 또는 409': (r) => r.status === 200 || r.status === 201 || r.status === 409,
    });

    // sleep(0.3);
}
