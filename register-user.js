import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export let options = {
    vus: 2, // 동시성 테스트를 위해 줄임
    duration: '10s',
    iterations: 100, // 총 100번의 테스트 시도
};

export let successCancel = new Counter('cancel_success');
export let successReject = new Counter('reject_success');
export let cancelConflict = new Counter('cancel_conflict');
export let rejectConflict = new Counter('reject_conflict');
export let cancelError = new Counter('cancel_error');
export let rejectError = new Counter('reject_error');

const baseUrl = 'http://localhost';
const postId = 2;

export function setup() {
    // 실제 테스트에서는 여기서 토큰을 가져오거나
    // 미리 준비된 토큰들을 반환
    return {
        cancelToken: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4MCIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc1MzQ0MjE0NiwiaWF0IjoxNzUzNDQwMzQ2fQ.nzZaMun4NiCGwx3BDQhigPTmWCRKmNdilZNsEeomTR8',
        authorToken: 'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDIiLCJ1c2VyUm9sZSI6IkFETUlOIiwiZXhwIjoxNzUzNDQyMDg1LCJpYXQiOjE3NTM0NDAyODV9.iEr9R8DEEi-k3b5lHUaPCcSEMSXxGcoxQCQ7m2TSkSY',
        testUsers: [] // 199명의 신청 완료된 유저 ID 배열 (80~278 등)
    };
}

export default function (data) {
    // 매번 다른 유저를 선택하여 테스트 (신청 완료된 유저 중에서)
    const testUserIds = [80, 81, 82, 83, 84]; // 실제로는 더 많은 유저 ID 필요
    const randomUserId = testUserIds[Math.floor(Math.random() * testUserIds.length)];

    // 동시 요청을 위한 Promise 배열
    const requests = [];

    // 취소 요청 (DELETE)
    const cancelRequest = new Promise((resolve) => {
        const cancelRes = http.del(
            `${baseUrl}/api/posts/${postId}/users/${randomUserId}/invite`,
            null,
            {
                headers: {
                    'Authorization': `Bearer ${data.cancelToken}`
                },
            }
        );
        resolve({ type: 'cancel', response: cancelRes });
    });

    // 거절 요청 (PATCH) - 거의 동시에 실행
    const rejectRequest = new Promise((resolve) => {
        const rejectRes = http.patch(
            `${baseUrl}/api/posts/${postId}/participate/users/${randomUserId}?status=REJECTED`,
            null,
            {
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${data.authorToken}`
                }
            }
        );
        resolve({ type: 'reject', response: rejectRes });
    });

    // 두 요청을 거의 동시에 실행
    requests.push(cancelRequest, rejectRequest);

    // 모든 요청 완료 대기
    const results = Promise.all(requests);

    results.then((responses) => {
        responses.forEach(({ type, response }) => {
            if (type === 'cancel') {
                if (response.status === 200 || response.status === 204) {
                    successCancel.add(1);
                } else if (response.status === 409) {
                    cancelConflict.add(1);
                } else {
                    cancelError.add(1);
                    console.log(`Cancel error: ${response.status}, body: ${response.body}`);
                }

                check(response, {
                    'cancel 요청 처리됨': (r) => r.status === 200 || r.status === 204 || r.status === 409,
                });
            } else if (type === 'reject') {
                if (response.status === 200 || response.status === 201) {
                    successReject.add(1);
                } else if (response.status === 409) {
                    rejectConflict.add(1);
                } else {
                    rejectError.add(1);
                    console.log(`Reject error: ${response.status}, body: ${response.body}`);
                }

                check(response, {
                    'reject 요청 처리됨': (r) => r.status === 200 || r.status === 201 || r.status === 409,
                });
            }
        });
    });

    sleep(0.1);
}

export function teardown(data) {
    console.log('=== 테스트 결과 ===');
    console.log(`취소 성공: ${successCancel.count}`);
    console.log(`거절 성공: ${successReject.count}`);
    console.log(`취소 충돌: ${cancelConflict.count}`);
    console.log(`거절 충돌: ${rejectConflict.count}`);
    console.log(`취소 에러: ${cancelError.count}`);
    console.log(`거절 에러: ${rejectError.count}`);
}