<!doctype html>
<html lang="ko">
<head>
    <meta charset="UTF-8"/>
    <title>로그인</title>
    <script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
    <script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
</head>
<body>
<div id="app">
    <h2>로그인</h2>
    <div>
        <label>아이디: </label>
        <input v-model="email" type="text"/>
    </div>
    <div>
        <label>비밀번호: </label>
        <input v-model="password" type="password"/>
    </div>
    <button @click="login">로그인</button>
</div>

<script>
    new Vue({
        el: '#app',
        data: {
            email: '',
            password: ''
        },
        methods: {
            login() {
                if (!this.email || !this.password) {
                    alert('아이디와 비밀번호를 입력하세요.');
                    return;
                }
                axios.post('/api/auth/login', {
                    email: this.email,
                    password: this.password
                }).then(res => {
                    // JWT 토큰 저장 (예: localStorage)
                    localStorage.setItem('jwtToken', res.data.data.token);
                    alert('로그인 성공! 채팅방으로 이동합니다.');
                    // 채팅방 페이지로 이동
                    window.location.href = '/chat/view';
                }).catch(err => {
                    alert('로그인 실패: ' + (err.response?.data?.message || err.message));
                });
            }
        }
    });
</script>
</body>
</html>