import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Counter } from 'k6/metrics';

export const options = {
    scenarios: {
        seed_posts: {
            executor: 'constant-arrival-rate',
            duration: __ENV.DURATION || '15m',
            rate: Number(__ENV.RATE || 140), // 분당 140개
            timeUnit: '1m',
            preAllocatedVUs: 200,
            maxVUs: 1000,
        },
    },
};


const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USER_MAX = Number(__ENV.USER_MAX || 8000);
const PASSWORD = __ENV.PASSWORD || 'password123!';
const postDur = new Trend('seed_post_duration');
const httpErrors = new Counter('seed_post_http_errors');

function pad(n){ return String(n).padStart(5,'0'); }
function headers(t){ return { 'Content-Type': 'application/json', ...(t ? { Authorization: `Bearer ${t}` } : {}) }; }
function tokenFrom(res){ try { return res.json('token') || res.json('data.token'); } catch { return null; } }

function loginAsHost() {
    // 호스트로 쓸 계정을 일부 구간에서만 선택(1~2000 범위 등)
    const uid = Math.floor(Math.random()*2000)+1;
    const email = `user_${pad(uid)}@test.com`;
    const r = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({ email, password: PASSWORD }), { headers: headers() });
    return tokenFrom(r);
}

function pickCapacity() {
    const r = Math.random();
    if (r < 0.3) return Math.floor(Math.random()*3)+3;     // 3~5
    if (r < 0.7) return Math.floor(Math.random()*3)+6;     // 6~8
    return Math.floor(Math.random()*4)+9;                   // 9~12
}

export default function () {
    const token = loginAsHost();
    if (!token) return;

    const now = Date.now();
    const payload = {
        title: `판교 모임 - ${now}`,
        timeSlot: new Date(now + 60*60*1000).toISOString(),
        publicContent: '판교 개발 밋업',
        maxParticipants: pickCapacity(),
        genderLimit: ['MALE','FEMALE','ALL'][Math.floor(Math.random()*3)],
        jobCategoryLimit: ['BACK_END_DEVELOPER','FRONT_END_DEVELOPER','FULL_STACK_DEVELOPER','ETC','ALL'][Math.floor(Math.random()*5)],
        ageLimit: ['AGE_20S','AGE_30S','AGE_40S','AGE_50S','ALL'][Math.floor(Math.random()*5)],
        tagIdList: [Math.floor(Math.random()*5) + 1, Math.floor(Math.random()*5) + 1],
        isFirstCome: Math.random() < 0.5,
    };

    const r = http.post(`${BASE_URL}/api/posts`, JSON.stringify(payload), { headers: headers(token) });
    postDur.add(r.timings.duration);
    if (r.status >= 400) httpErrors.add(1, { status: String(r.status) });
    check(r, { 'post created(2xx)': (res) => res.status >= 200 && res.status < 300 });

    sleep(0.05);
}
