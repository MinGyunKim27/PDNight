import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export let options = {
    vus: 50, // 동시성 테스트를 위해 적절한 수준으로 설정
    duration: '10s',
};

// 커스텀 카운터 메트릭
export let successCancel = new Counter('cancel_success');
export let successReject = new Counter('reject_success');
export let cancelConflict = new Counter('cancel_conflict');
export let rejectConflict = new Counter('reject_conflict');
export let cancelError = new Counter('cancel_error');
export let rejectError = new Counter('reject_error');
export let concurrencyIssue = new Counter('concurrency_issue');

const baseUrl = 'http://localhost:8081';
const postId = 2; // postId를 2로 변경

export function setup() {
    const users = [];

    // 실제 2번 포스트에 PENDING 상태로 신청한 유저들의 ID를 여기에 입력
    // 현재는 테스트를 위해 임시로 일부 유저만 포함
    const pendingUserIds = Array.from({ length: 200 }, (_, i) => i + 1);

    console.log(`로그인 시작 - ${pendingUserIds.length}명의 PENDING 유저들...`);

    // PENDING 상태인 유저들만 로그인
    for (let userId of pendingUserIds) {
        const email = `user${userId}@naver.com`;
        const password = 'Test@1234';

        const res = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({ email, password }), {
            headers: { 'Content-Type': 'application/json' },
        });

        let token = null;
        try {
            const json = res.json();
            token = json?.data?.token;
        } catch (e) {
            console.error(`Login JSON parse error for user${userId}:`, e);
        }

        if (token) {
            users.push({ token, id: userId });
            console.log(`User${userId} 로그인 성공`);
        } else {
            console.error(`Login failed for user${userId}, status: ${res.status}`);
        }
    }

    // 게시글 작성자 (authorId: 201) 로그인
    const authorRes = http.post(`${baseUrl}/api/auth/login`, JSON.stringify({
        email: 'user201@naver.com',
        password: 'Test@1234'
    }), {
        headers: { 'Content-Type': 'application/json' },
    });

    let authorToken = null;
    try {
        const json = authorRes.json();
        authorToken = json?.data?.token;
    } catch (e) {
        console.error('Author login JSON parse error:', e);
    }

    if (authorToken) {
        console.log('Author(user201) 로그인 성공');
    } else {
        console.error(`Author login failed, status: ${authorRes.status}, body: ${authorRes.body}`);
    }

    console.log(`Setup 완료 - 총 ${users.length}명의 유저 토큰 준비됨`);

    return {
        users: users,
        authorToken: authorToken
    };
}

export default function (data) {
    if (!data.users || data.users.length === 0) {
        console.error('사용 가능한 유저가 없습니다');
        return;
    }

    if (!data.authorToken) {
        console.error('작성자 토큰이 없습니다');
        return;
    }

    // 랜덤하게 ACCEPTED 상태인 유저 선택
    const randomUser = data.users[Math.floor(Math.random() * data.users.length)];
    const userId = randomUser.id;
    const userToken = randomUser.token;

    console.log(`Testing with user ID: ${userId}`);

    // 동시 요청을 위한 시작 시간 동기화
    const startTime = Date.now();

    // 취소 요청 (DELETE) - 참가자 본인이 취소
    const cancelPromise = new Promise((resolve) => {
        // 약간의 랜덤 지연으로 실제 동시성 상황 모방
        const delay = Math.random() * 10; // 0-10ms 랜덤 지연
        setTimeout(() => {
            const cancelRes = http.del(
                `${baseUrl}/api/posts/${postId}/users/${userId}/invite`,
                null,
                {
                    headers: {
                        'Authorization': `Bearer ${userToken}`
                    },
                }
            );
            resolve({
                type: 'cancel',
                response: cancelRes,
                userId: userId,
                timestamp: Date.now() - startTime
            });
        }, delay);
    });

    // 거절 요청 (PATCH) - 게시자가 거절
    const rejectPromise = new Promise((resolve) => {
        const delay = Math.random() * 10; // 0-10ms 랜덤 지연
        setTimeout(() => {
            const rejectRes = http.patch(
                `${baseUrl}/api/posts/${postId}/participate/users/${userId}?status=REJECTED`,
                null,
                {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${data.authorToken}`
                    }
                }
            );
            resolve({
                type: 'reject',
                response: rejectRes,
                userId: userId,
                timestamp: Date.now() - startTime
            });
        }, delay);
    });

    // 두 요청을 동시에 실행하고 결과 대기
    Promise.all([cancelPromise, rejectPromise]).then((responses) => {
        let cancelSuccess = false;
        let rejectSuccess = false;

        responses.forEach(({ type, response, userId, timestamp }) => {
            if (type === 'cancel') {
                if (response.status === 200 || response.status === 204) {
                    successCancel.add(1);
                    cancelSuccess = true;
                    console.log(`[${timestamp}ms] User ${userId} 취소 성공`);
                } else if (response.status === 409) {
                    cancelConflict.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 취소 실패 (409 - CANNOT_CANCEL)`);
                } else if (response.status === 404) {
                    cancelConflict.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 취소 실패 (404 - 이미 없음)`);
                } else if (response.status === 400) {
                    cancelConflict.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 취소 실패 (400 - 신청되어있지 않음)`);
                } else if (response.status === 400) {
                    rejectConflict.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 거절 실패 (400 - 신청되어있지 않음)`);
                } else {
                    cancelError.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 취소 에러: ${response.status}, body: ${response.body}`);
                }

                check(response, {
                    'cancel 요청 적절히 처리됨': (r) => r.status === 200 || r.status === 204 || r.status === 409 || r.status === 404 || r.status === 400,
                });

            } else if (type === 'reject') {
                if (response.status === 200 || response.status === 201) {
                    successReject.add(1);
                    rejectSuccess = true;
                    console.log(`[${timestamp}ms] User ${userId} 거절 성공`);
                } else if (response.status === 409) {
                    rejectConflict.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 거절 실패 (409 - 이미 처리됨)`);
                } else if (response.status === 404) {
                    rejectConflict.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 거절 실패 (404 - 참가자 없음)`);
                } else {
                    rejectError.add(1);
                    console.log(`[${timestamp}ms] User ${userId} 거절 에러: ${response.status}, body: ${response.body}`);
                }

                check(response, {
                    'reject 요청 적절히 처리됨': (r) => r.status === 200 || r.status === 201 || r.status === 409 || r.status === 404 || r.status === 400,
                });
            }
        });

        // 🚨 동시성 검증: 둘 다 성공하면 분산락이 제대로 작동하지 않은 것
        if (cancelSuccess && rejectSuccess) {
            concurrencyIssue.add(1);
            console.log(`🚨🚨🚨 동시성 문제 발견! User ${userId}에 대해 취소와 거절이 모두 성공함! 🚨🚨🚨`);
        }
    }).catch((error) => {
        console.error(`Promise 처리 중 오류 발생:`, error);
    });

    sleep(0.2);
}

export function teardown(data) {
    console.log('\n===  동시성 테스트 결과 ===');
    console.log(`취소 성공: ${successCancel ? successCancel.count : 0}`);
    console.log(`거절 성공: ${successReject ? successReject.count : 0}`);
    console.log(`취소 충돌 (409/404): ${cancelConflict ? cancelConflict.count : 0}`);
    console.log(`거절 충돌 (409/404): ${rejectConflict ? rejectConflict.count : 0}`);
    console.log(`취소 에러: ${cancelError ? cancelError.count : 0}`);
    console.log(`거절 에러: ${rejectError ? rejectError.count : 0}`);
    console.log(` 동시성 문제 발생 횟수: ${concurrencyIssue ? concurrencyIssue.count : 0}`);

    const concurrencyCount = concurrencyIssue ? concurrencyIssue.count : 0;
    if (concurrencyCount > 0) {
        console.log('\n 분산락이 제대로 작동하지 않았습니다!');
    } else {
        console.log('\n 분산락이 정상적으로 동시성을 제어했습니다!');
    }
}