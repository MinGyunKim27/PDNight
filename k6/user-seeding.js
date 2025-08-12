import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export const options = {
    scenarios: {
        seed_users: {
            executor: 'per-vu-iterations',
            vus: Number(__ENV.VUS || 200),
            iterations: Number(__ENV.ITER || 40), // VU당 40회 → 총 200*40=8000
            maxDuration: '30m',
        },
    },
};


const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const START = Number(__ENV.START || 1);     // user_00001부터
const PASSWORD = __ENV.PASSWORD || 'password123!';
const httpErrors = new Counter('seed_user_http_errors');

function pad(n){ return String(n).padStart(5,'0'); }

export default function () {
    const seq = START + (__VU-1) * Number(__ENV.ITER || 40) + (__ITER - 1);
    const email = `user_${pad(seq)}@test.com`;
    const body = JSON.stringify({
        email, password: PASSWORD,
        name: `name_${seq}`, nickname: `nick_${seq}`,
        gender: 'MALE', age: 28,
        jobCategory: 'BACK_END_DEVELOPER'
    });

    const r = http.post(`${BASE_URL}/api/auth/signup`, body, {
        headers: { 'Content-Type': 'application/json' }
    });

    if (r.status >= 400) httpErrors.add(1, { status: String(r.status) });
    check(r, { 'signup ok(201/200/409)': (res) => [200,201,409].includes(res.status) });
    // 너무 빠르게 때리지 않도록 약간의 슬립
    sleep(0.05);
}
