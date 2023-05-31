// package com.example.zzan.webChat.service.ChatService;
//
// import com.example.zzan.webChat.dto.ChatRoomDto;
// import com.example.zzan.webChat.dto.ChatRoomMap;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
//
//
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;
//
//
// @Slf4j
// @RequiredArgsConstructor
// @Service
// public class MsgChatService {
//
//     public ChatRoomDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {
//         // roomName 와 roomPwd 로 chatRoom 빌드 후 return
//         ChatRoomDto room = ChatRoomDto.builder()
//                 .roomId(UUID.randomUUID().toString())
//                 .roomName(roomName)
//                 .roomPwd(roomPwd) // 채팅방 패스워드
//                 .secretChk(secretChk) // 채팅방 잠금 여부
//                 .userCount(0) // 채팅방 참여 인원수
//                 .maxUserCnt(maxUserCnt) // 최대 인원수 제한
//                 .build();
//
//         room.setUserList(new HashMap<String, String>());
//
//         // msg 타입이면 ChatType.MSG
//         room.setChatType(ChatRoomDto.ChatType.MSG);
//
//         // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
//         ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);
//
//         return room;
//     }
//
// }
