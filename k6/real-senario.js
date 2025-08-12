import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Trend } from 'k6/metrics';

// ===== 환경 변수 =====
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const PASSWORD = __ENV.PASSWORD || 'password123!';
const USERS    = Number(__ENV.USERS || 8000);     // 사전 생성된 계정 수
const THINK    = Number(__ENV.THINK || 0.3);      // 사람 같은 대기

// 엔드포인트 (네 API에 맞춤)
const PATH = {
    login: '/api/auth/login',
    signup: '/api/auth/signup', // 필요 시 사용
    // 게시글
    createPost: '/api/posts',
    listPosts: '/api/posts?size=20&page=1',
    postDetail: (id) => `/api/posts/${id}`,
    myPosts: '/api/my/written-posts',
    // 좋아요/신청/댓글/팔로우
    like: (id) => `/api/posts/${id}/likes`,
    unlike: (id) => `/api/posts/${id}/likes`,
    apply: (id) => `/api/posts/${id}/participate`,          // 바디 없음
    cancel: (id) => `/api/posts/${id}/participate`,          // DELETE
    comment: (id) => `/api/posts/${id}/comments`,
    reply: (id, cid) => `/api/posts/${id}/comments/${cid}/comments`,
    follow: (uid) => `/api/users/${uid}/follow`,
    unfollow: (uid) => `/api/users/${uid}/follow`,
    // 리뷰
    postUserReview: (postId, userId) => `/api/posts/${postId}/participants/${userId}/review`,
    // 기타 조회
    confirmedParticipants: (postId) => `/api/posts/${postId}/participate/confirmed`,
    myConfirmed: '/api/my/confirmed-posts',
};

// ===== 메트릭/유틸 =====
const httpErrors = new Counter('http_errors');
const actLatency = new Trend('act_latency');

function headers(token) {
    const h = { 'Content-Type': 'application/json' };
    if (token) h['Authorization'] = `Bearer ${token}`;
    return h;
}
function think() { sleep(Math.random()*0.4 + THINK); }
function stat(action, res) {
    if (res) {
        actLatency.add(res.timings.duration, { action });
        if (res.status >= 400) httpErrors.add(1, { action, status: String(res.status) });
    }
}
function extractToken(res) {
    try {
        const j = res.json();
        return j?.token || j?.data?.token || null; // 로그인 응답: token
    } catch { return null; }
}
function loginAsPoolUser() {
    const uid = Math.floor(Math.random()*USERS) + 1;
    const email = `user_${String(uid).padStart(5, '0')}@test.com`;
    const r = http.post(`${BASE_URL}${PATH.login}`, JSON.stringify({ email, password: PASSWORD }), { headers: headers() });
    const token = extractToken(r);
    check({ ok: !!token }, { 'token issued': v => v.ok === true });
    return { token, uid, email };
}
function parsePage(res) {
    if (!res || res.status !== 200) return { items: [], page: null };
    try {
        const d = res.json('data') || {};
        return {
            items: Array.isArray(d.contents) ? d.contents : [],
            page: d ? { totalElements: d.totalElements, totalPages: d.totalPages, size: d.size, number: d.number } : null,
        };
    } catch { return { items: [], page: null }; }
}


// ===== 옵션: 2시간 =====
export const options = {
    scenarios: {
        attendees: {
            executor: 'ramping-arrival-rate',
            timeUnit: '1s',
            preAllocatedVUs: 400,
            maxVUs: 4000,
            startRate: 20,
            stages: [
                { duration: '5m',  target: 40 }, // 워밍업
                { duration: '25m', target: 60 }, // 점증
                { duration: '60m', target: 60 }, // 본구간
                { duration: '20m', target: 50 }, // 소폭 감소
                { duration: '10m', target: 30 }, // 쿨다운
            ],
            exec: 'attendeeFlow',
            tags: { role: 'attendee' },
            gracefulStop: '30s',
        },
        hosts: {
            executor: 'constant-arrival-rate',
            timeUnit: '1s',
            rate: Number(__ENV.HOST_RPS || 6), // 호스트는 낮은 비율
            preAllocatedVUs: 120,
            maxVUs: 1000,
            duration: '120m',
            exec: 'hostFlow',
            tags: { role: 'host' },
            gracefulStop: '30s',
        },
        reviews: {
            executor: 'constant-arrival-rate',
            timeUnit: '1s',
            rate: Number(__ENV.REVIEW_RPS || 8),
            preAllocatedVUs: 100,
            maxVUs: 1000,
            duration: '30m',
            startTime: '90m', // 마지막 30분 집중
            exec: 'reviewFlow',
            tags: { role: 'review' },
        },
    },
    thresholds: {
        'http_req_failed': ['rate<0.02'],
        'http_req_duration{role:attendee}': ['p(95)<700', 'p(99)<1500'],
        'http_req_duration{role:host}': ['p(95)<900', 'p(99)<1800'],
    },
};

// ===== 행동 함수들 =====
function createPost(token) {
    // 네가 제공한 생성 바디 스키마 사용
    const now = Date.now();
    const payload = {
        title: `판교 모임 - ${now}`,
        timeSlot: new Date(now + 60*60*1000).toISOString(), // 1시간 뒤
        publicContent: '판교에서 개발 밋업해요!',
        maxParticipants: [3,4,5,6,7,8,9,10,11,12][Math.floor(Math.random()*10)],
        genderLimit: ['MALE','FEMALE','ALL'][Math.floor(Math.random()*3)],
        jobCategoryLimit: ['BACK_END_DEVELOPER','FRONT_END_DEVELOPER','FULL_STACK_DEVELOPER','ETC','ALL'][Math.floor(Math.random()*5)],
        ageLimit: ['AGE_20S','AGE_30S','AGE_40S','AGE_50S','ALL'][Math.floor(Math.random()*5)],
        isFirstCome: Math.random() < 0.5, // 선착순 절반
    };
    const r = http.post(`${BASE_URL}${PATH.createPost}`, JSON.stringify(payload), { headers: headers(token) });
    stat('createPost', r);
    return r;
}

function browseList(token) {
    const r = http.get(`${BASE_URL}/api/posts?size=20&page=0`, { headers: headers(token) });
    stat('listPosts', r);
    return r;
}

function viewDetail(token, postId) {
    const r = http.get(`${BASE_URL}${PATH.postDetail(postId)}`, { headers: headers(token) });
    stat('postDetail', r);
    return r;
}

function likePost(token, postId) {
    const r = http.post(`${BASE_URL}${PATH.like(postId)}`, null, { headers: headers(token) });
    stat('like', r);
    return r;
}
function applyPost(token, postId) {
    const r = http.post(`${BASE_URL}${PATH.apply(postId)}`, null, { headers: headers(token) }); // 바디 없음
    stat('apply', r);
    return r;
}
function cancelApply(token, postId) {
    const r = http.del(`${BASE_URL}${PATH.cancel(postId)}`, null, { headers: headers(token) });
    stat('cancel', r);
    return r;
}
function commentPost(token, postId) {
    const msgs = ['좋아요!', '시간 가능해요', '장소 문의드립니다', '참여 희망합니다 🙂'];
    const body = JSON.stringify({ content: msgs[Math.floor(Math.random()*msgs.length)] });
    const r = http.post(`${BASE_URL}${PATH.comment(postId)}`, body, { headers: headers(token) });
    stat('comment', r);
    return r;
}
function followUser(token) {
    const target = Math.floor(Math.random()*Math.max(USERS, 1000)) + 1;
    const r = http.post(`${BASE_URL}${PATH.follow(target)}`, null, { headers: headers(token) });
    stat('follow', r);
    return r;
}

function randomPostIdFromList(listRes) {
    if (!listRes || listRes.status !== 200) return Math.floor(Math.random()*200000)+1;
    const { items } = parsePage(listRes);               // ✅ contents 사용
    if (!items.length) return Math.floor(Math.random()*200000)+1;
    const item = items[Math.floor(Math.random()*items.length)];
    return item?.postId || item?.id || Math.floor(Math.random()*200000)+1;
}


function myConfirmedPosts(token) {
    const r = http.get(`${BASE_URL}/api/my/confirmed-posts`, { headers: headers(token) });
    stat('myConfirmed', r);
    if (r.status !== 200) return [];
    try {
        return r.json('data.contents') || [];
    } catch { return []; }
}

function confirmedParticipantsOfPost(token, postId) {
    const r = http.get(`${BASE_URL}/api/posts/${postId}/participate/confirmed?page=0&size=50`, { headers: headers(token) });
    stat('confirmedParticipants', r);
    return parsePage(r).items;                           // 페이지 공통 파서
}


function pickReviewTarget(meUserId, participants, fallbackAuthorId) {
    // participants: [{userId: number, ...}] 형식이라고 가정
    const others = participants.map(p => p.userId || p.id).filter(Boolean).filter(uid => uid !== meUserId);
    if (others.length) return others[Math.floor(Math.random()*others.length)];
    return fallbackAuthorId; // 참가자 없으면 작성자에게
}

function sendUserReview(token, postId, targetUserId) {
    const body = JSON.stringify({
        rating: 4 + Math.floor(Math.random()*2),
        content: '좋은 모임이었습니다!'
    });
    const r = http.post(`${BASE_URL}/api/posts/${postId}/participants/${targetUserId}/review`, body, { headers: headers(token) });
    stat('review', r);
    check(r, { 'review 2xx': rr => rr.status >= 200 && rr.status < 300 });
}


// ===== 시나리오: 참가자 =====
export function attendeeFlow() {
    const { token } = loginAsPoolUser();
    if (!token) return;

    // 1) 목록 탐색
    const list = browseList(token); think();

    // 2) 상세 1~3개 확인
    const count = 1 + Math.floor(Math.random()*3);
    for (let i=0; i<count; i++) {
        const id = randomPostIdFromList(list);
        const d = viewDetail(token, id);

        // 40% 좋아요
        if (Math.random() < 0.40) likePost(token, id);

        // 45% 신청 (자동 확정은 서버가 maxParticipants 다 차면 처리)
        if (Math.random() < 0.45) {
            const a = applyPost(token, id);
            // 401/403 시 1회 재로그인 후 재시도(옵션)
            if ([401,403].includes(a.status)) {
                const relog = loginAsPoolUser();
                if (relog.token) applyPost(relog.token, id);
            }
        } else if (Math.random() < 0.15) {
            // 15% 취소
            cancelApply(token, id);
        }

        // 30% 댓글
        if (Math.random() < 0.30) commentPost(token, id);

        think();
    }

    // 15% 팔로우
    if (Math.random() < 0.15) followUser(token);
    think();
}

// ===== 시나리오: 호스트 =====
export function hostFlow() {
    const { token } = loginAsPoolUser();
    if (!token) return;

    // 1) 10% 확률로 게시글 등록
    if (Math.random() < 0.10) { createPost(token); think(); }

    // 2) 내가 작성한 게시글 조회 → 상세→(정원 도달 여부는 서버가 판단/자동 확정)
    const mine = http.get(`${BASE_URL}${PATH.myPosts}`, { headers: headers(token) });
    stat('myPosts', mine);
    if (mine.status === 200) {
        const { items } = parsePage(mine);                    // ✅ contents 사용
        (items || []).slice(0, 5).forEach(p => {
            const id = p.postId || p.id;                        // ✅ postId 우선
            if (!id) return;
            viewDetail(token, id);
            if (Math.random() < 0.3) {
                const conf = http.get(`${BASE_URL}${PATH.confirmedParticipants(id)}`, { headers: headers(token) });
                stat('confirmedParticipants', conf);
            }
            think();
        });
    }
}

// ===== 시나리오: 리뷰(후반 30분) =====
export function reviewFlow() {
    const { token, uid: me } = loginAsPoolUser();
    if (!token) return;

    // 확정된 내 모임 목록 (data.contents 기준)
    const confirmed = myConfirmedPosts(token);
    if (!confirmed.length) { think(); return; }

    // 하나 고르고 → 확정 참여자 받아서 대상 선택
    const post = confirmed[Math.floor(Math.random()*confirmed.length)];
    const postId = post.postId;         // 샘플 구조에 맞춤
    const authorId = post.authorId;     // 필요시 fallback

    if (!postId) { think(); return; }

    const parts = confirmedParticipantsOfPost(token, postId);
    const targetUserId = pickReviewTarget(me, parts, authorId);
    if (targetUserId) {
        sendUserReview(token, postId, targetUserId);
    }

    think();
}
