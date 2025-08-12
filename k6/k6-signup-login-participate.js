import http from 'k6/http';
import { sleep, check } from 'k6';
import { Counter } from 'k6/metrics';

export const options = {
    scenarios: {
        signup_login_participate: {
            executor: 'ramping-vus',
            stages: [
                { duration: '1m', target: 1000 },
                { duration: '2m', target: 1000 },
                { duration: '1m', target: 0 },
            ],
            gracefulRampDown: '15s',
            gracefulStop: '30s',
        },
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PARTICIPATE_PATH = __ENV.PARTICIPATE_PATH || '/api/posts/{postId}/participate';
const POST_ID_FROM = 1;
const POST_ID_TO   = 100;
const THINK = parseFloat(__ENV.THINK || '0.3');
const DO_SIGNUP = (__ENV.DO_SIGNUP || 'true') === 'true';   // 필요 없으면 false
const DO_CANCEL = (__ENV.DO_CANCEL || 'true') === 'true';   // 취소 호출 비활성화 가능

// VU별 런타임에 유지되는 캐시
let TOKEN = null;

const httpErrors = new Counter('http_errors');

function headers(token) {
    const h = { 'Content-Type': 'application/json' };
    if (token) h['Authorization'] = `Bearer ${token}`;
    return h;
}

function extractToken(res) {
    try {
        return res.json('data.token') || res.json('accessToken') || res.json('token') || null;
    } catch { return null; }
}

function stat(name, res) {
    if (!res) return;
    if (res.status >= 400) httpErrors.add(1, { ep: name, status: String(res.status) });
}

function signupOncePerVU(email, password, name, nickname, gender, age, jobCategory) {
    if (!DO_SIGNUP || __ITER !== 0) return;
    const body = JSON.stringify({ email, password, name, nickname, gender, age, jobCategory });
    const res = http.post(`${BASE_URL}/api/auth/signup`, body, { headers: headers() });
    stat('signup', res);
    check(res, { 'signup ok(200/201/409)': r => [200, 201, 409].includes(r.status) });
    sleep(THINK);
}

function ensureLogin(email, password) {
    if (TOKEN) return TOKEN;
    let last = null;
    for (let i = 0; i < 2 && !TOKEN; i++) {
        const res = http.post(`${BASE_URL}/api/auth/login`, JSON.stringify({ email, password }), { headers: headers() });
        last = res;
        TOKEN = extractToken(res);
        if (!TOKEN) sleep(0.2);
    }
    stat('login', last);
    check(last || {}, { 'login token acquired': _ => !!TOKEN });
    return TOKEN;
}

function pickPostId() {
    return Math.floor(Math.random() * (POST_ID_TO - POST_ID_FROM + 1)) + POST_ID_FROM;
}

export default function () {
    const email = `user_${__VU}@test.com`;
    const password = 'password123!';
    const name = `name_${__VU}`;
    const nickname = `nickname_${__VU}`;
    const gender = 'MALE';
    const age = 30;
    const jobCategory = 'BACK_END_DEVELOPER';

    // 1) VU당 1회만 회원가입
    signupOncePerVU(email, password, name, nickname, gender, age, jobCategory);

    // 2) VU당 최초 1회만 로그인 → TOKEN 캐시
    const token = ensureLogin(email, password);
    if (!token) return;

    sleep(THINK);

    // 3) 참여 API
    const postId = pickPostId();
    const path = PARTICIPATE_PATH.replace('{postId}', String(postId));
    let p = http.post(`${BASE_URL}${path}`, JSON.stringify({ message: '참여 신청합니다!' }), { headers: headers(token) });

    // 토큰 만료 대비: 401/403이면 1회 재로그인 후 재시도
    if ([401, 403].includes(p.status)) {
        TOKEN = null;
        ensureLogin(email, password);
        p = http.post(`${BASE_URL}${path}`, JSON.stringify({ message: '참여 신청합니다!' }), { headers: headers(TOKEN) });
    }

    stat('participate', p);
    check(p, { 'participate ok(200/201/204/409)': r => [200, 201, 204, 409].includes(r.status) });

    sleep(THINK);

    // 4) (옵션) 취소 API
    if (DO_CANCEL) {
        const path2 = PARTICIPATE_PATH.replace('{postId}', String(postId));
        const p2 = http.del(`${BASE_URL}${path2}`, null, { headers: headers(TOKEN) });
        stat('cancel', p2);
        check(p2, { 'cancel ok(200/204/404)': r => [200, 204, 404].includes(r.status) });
        sleep(THINK);
    }
}
