<!doctype html>
<html lang="en">
<head>
    <title>채팅방 목록</title>
    <meta charset="utf-8">
    <script src="/webjars/vue/2.5.16/dist/vue.min.js"></script>
    <script src="/webjars/axios/0.17.1/dist/axios.min.js"></script>
</head>
<body>
<div id="app">
    <h2>채팅방 목록</h2>

    <!-- 채팅방 생성 -->
    <div>
        <input v-model="newRoomName" placeholder="채팅방 이름 입력">
        <button @click="createRoom">채팅방 생성</button>
    </div>

    <!-- 채팅방 리스트 -->
    <ul>
        <li v-for="room in chatrooms" :key="room.id">
            {{ room.chatRoomName }}
            <button @click="enterRoom(room.id)">입장</button>
        </li>
    </ul>
</div>

<script>
    new Vue({
        el: '#app',
        data: {
            chatrooms: [],
            newRoomName: ''
        },
        created() {
            this.fetchRooms();
        },
        methods: {
            fetchRooms() {
                axios.get('/chat/rooms')
                    .then(response => {
                        this.chatrooms = response.data;
                    });
            },
            createRoom() {
                if (this.newRoomName.trim() === '') return;
                axios.post('/chat/room', null, {
                    params: { name: this.newRoomName }
                }).then(() => {
                    this.newRoomName = '';
                    this.fetchRooms();
                });
            },
            enterRoom(roomId) {
                const sender = prompt("대화명을 입력해주세요.");
                if (sender && sender.trim() !== "") {
                    localStorage.setItem("wschat.sender", sender);
                    localStorage.setItem("wschat.roomId", roomId);
                    window.location.href = "/chat/room/enter/" + roomId;
                }
            }
        }
    });
</script>
</body>
</html>