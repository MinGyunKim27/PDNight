<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>채팅방</title>
    <script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
    <script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
    <script src="/webjars/sockjs-client/1.1.2/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/2.3.3-1/stomp.min.js"></script>
    <style>
        html, body {
            height: 100%;
            margin: 0;
            font-family: sans-serif;
        }

        #chat {
            display: flex;
            flex-direction: column;
            height: 100%;
        }

        .chat-header {
            padding: 1rem;
            background-color: #f4f4f4;
            border-bottom: 1px solid #ccc;
        }

        .chat-messages {
            flex: 1;
            overflow-y: auto;
            padding: 1rem;
            background-color: #fafafa;
        }

        .chat-input {
            padding: 0.5rem;
            border-top: 1px solid #ccc;
            display: flex;
            background-color: #fff;
        }

        .chat-input input {
            flex: 1;
            padding: 0.5rem;
            font-size: 1rem;
        }

        .chat-input button {
            margin-left: 0.5rem;
            padding: 0.5rem 1rem;
        }

        [v-cloak] {
            display: none;
        }
    </style>
</head>
<body>
<div id="chat" v-cloak>
    <!-- 채팅방 이름 (고정 상단) -->
    <div class="chat-header">
        <h2>{{ room.chatRoomName }}</h2>
    </div>

    <!-- 메시지 출력 영역 (스크롤 가능) -->
    <div class="chat-messages" ref="chatContainer">
        <div v-for="msg in messages" :key="msg.id">
            <strong>{{ msg.sender }}:</strong> {{ msg.message }}
        </div>
    </div>

    <!-- 입력창 (고정 하단) -->
    <div class="chat-input">
        <input v-model="message" @keypress.enter="sendMessage" placeholder="메시지를 입력하세요"/>
        <button @click="sendMessage">보내기</button>
    </div>
</div>

<script>
    const token = localStorage.getItem('jwtToken');
    if (token) {
        axios.defaults.headers.common['Authorization'] = 'Bearer ' + token;
    }

    let sock = new SockJS("/ws-stomp");
    let ws = Stomp.over(sock);
    let reconnect = 0;

    new Vue({
        el: '#chat',
        data: {
            roomId: '',
            room: {},
            sender: '',
            message: '',
            messages: []
        },
        created() {
            this.roomId = localStorage.getItem("wschat.roomId");
            this.sender = localStorage.getItem("wschat.sender");
            // 1) 채팅방 참여자 목록에 본인 등록 API 호출
            axios.get(`/chatRoom/enter/${roomId}`)
                .then(() => {
                    // 참여자 등록 완료 후 다른 초기화 작업 진행
                    this.fetchRoomInfo();
                    this.fetchChatHistory();
                    this.connectWS();
                })
                .catch(err => {
                    alert("참가 불가능한 채팅방입니다.");
                    window.location.href = '/chat/view';
                });
        },
        updated() {
            // 메시지 업데이트 시 자동 스크롤
            this.$nextTick(() => {
                const container = this.$refs.chatContainer;
                container.scrollTop = container.scrollHeight;
            });
        },
        methods: {
            fetchChatHistory() {
                axios.get(`/chat/record`, {
                    params: {
                        roomId: this.roomId
                    }
                }).then(res => {
                    res.data.forEach(msg => {
                        this.messages.push({
                            id: Date.now() + Math.random(), // 임시 고유값
                            sender: msg.messageType === 'ENTER' ? '[알림]' : msg.sender,
                            message: msg.message
                        });
                    });
                }).catch(err => {
                    console.error("채팅 내역 불러오기 실패", err);
                });
            },

            fetchRoomInfo() {
                axios.get("/chat/room/" + this.roomId)
                    .then(res => {
                        this.room = res.data;
                    });
            },
            connectWS() {
                ws.connect({}, frame => {
                    ws.subscribe("/sub/chat/room/" + this.roomId, message => {
                        let recv = JSON.parse(message.body);
                        this.recvMessage(recv);
                    });

                    // 입장 메시지
                    ws.send("/pub/chat/message", {}, JSON.stringify({
                        messageType: 'ENTER',
                        roomId: this.roomId,
                        sender: this.sender
                    }));
                }, error => {
                    if (reconnect++ < 5) {
                        setTimeout(() => {
                            sock = new SockJS("/ws-stomp");
                            ws = Stomp.over(sock);
                            this.connectWS();
                        }, 10000);
                    }
                });
            },
            recvMessage(recv) {
                this.messages.push({
                    id: Date.now() + Math.random(),
                    sender: recv.messageType === 'ENTER' ? '[알림]' : recv.sender,
                    message: recv.message
                });
            },
            sendMessage() {
                if (!this.message.trim()) return;
                ws.send("/pub/chat/message", {}, JSON.stringify({
                    messageType: 'TALK',
                    roomId: this.roomId,
                    sender: this.sender,
                    message: this.message
                }));
                this.message = '';
            }
        }
    });
</script>
</body>
</html>
