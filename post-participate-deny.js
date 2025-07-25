import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

export let options = {
    vus: 50,
    duration: '5s',
};

export let successCancel = new Counter('cancel_success');
export let successReject = new Counter('reject_success');
export let conflict = new Counter('conflict');

const baseUrl = 'http://localhost:8080';
const postId = 1;  // 테스트할 게시글 ID
const userId = 80;  // 취소 시도 유저 ID (신청 완료된 유저 중 하나)
const authorId = 201; // 게시자(거절 권한자)

export default function () {
    // 취소 요청 (신청자 역할)
    let cancelRes = http.del(`${baseUrl}/api/posts/${postId}/users/${userId}/invite`, null, {
        headers: { 'Authorization': `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiI4MCIsInVzZXJSb2xlIjoiVVNFUiIsImV4cCI6MTc1MzQ0MjE0NiwiaWF0IjoxNzUzNDQwMzQ2fQ.nzZaMun4NiCGwx3BDQhigPTmWCRKmNdilZNsEeomTR8` },
    });

    if (cancelRes.status === 200 || cancelRes.status === 204) {
        successCancel.add(1);
    } else if (cancelRes.status === 409) {
        conflict.add(1);
    }

    // 거의 동시에 거절 요청 (게시자 역할)
    let rejectRes = http.patch(
        `${baseUrl}/api/posts/${postId}/participate/users/${userId}?status=REJECTED`,
        null,  // PATCH body는 없음
        {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyMDIiLCJ1c2VyUm9sZSI6IkFETUlOIiwiZXhwIjoxNzUzNDQyMDg1LCJpYXQiOjE3NTM0NDAyODV9.iEr9R8DEEi-k3b5lHUaPCcSEMSXxGcoxQCQ7m2TSkSY`
            }
        }
    );

    if (rejectRes.status === 200 || rejectRes.status === 201) {
        successReject.add(1);
    } else if (rejectRes.status === 409) {
        conflict.add(1);
    }

    check(cancelRes, {
        'cancel 응답 성공': (r) => r.status === 200 || r.status === 204,
    });

    check(rejectRes, {
        'reject 응답 성공': (r) => r.status === 200 || r.status === 201,
    });

    sleep(0.2);
}
