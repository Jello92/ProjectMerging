package com.example.zzan.webRtc.service.ChatService;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.room.dto.RoomResponseDto;
// import com.example.zzan.webChat.dto.RoomResponseDto;
import com.example.zzan.webRtc.dto.UserListMap;
import com.example.zzan.webRtc.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class RtcChatService {

//// repository substitution since this is a very simple realization
//
// public RoomResponseDto createChatRoom(String roomName, String roomPwd, boolean secretChk, int maxUserCnt) {
// // roomName 와 roomPwd 로 chatRoom 빌드 후 return
// RoomResponseDto room = RoomResponseDto()
// .roomId(UUID.randomUUID().toString())
// .roomName(roomName)
// .roomPwd(roomPwd) // 채팅방 패스워드
// .secretChk(secretChk) // 채팅방 잠금 여부
// .userCount(0) // 채팅방 참여 인원수
// .maxUserCnt(maxUserCnt) // 최대 인원수 제한
// .build();
//
// room.setUserList(new HashMap<String, WebSocketSession>());
//
// // msg 타입이면 ChatType.MSG
// room.setChatType(RoomResponseDto.ChatType.RTC);
//
//
// // map 에 채팅룸 아이디와 만들어진 채팅룸을 저장
// ChatRoomMap.getInstance().getChatRooms().put(room.getRoomId(), room);
//
// return room;
// }

    public Map<Long, WebSocketSession> getUser (RoomResponseDto roomResponseDto) {

        Optional<RoomResponseDto> roomDto = Optional.ofNullable(roomResponseDto);

        return (Map<String, WebSocketSession>) roomDto.get().getUserList();
    }


    public ResponseDto addUser(RoomResponseDto roomResponseDto, Long userId, WebSocketSession session) {
        roomResponseDto.setUserList(new HashMap<>());
        Map<Long, WebSocketSession> userList = roomResponseDto.getUserList();
        userList.put(userId, session);
        return ResponseDto.setSuccess("유저 리스트가 추가 되었습니다", userList);
    }

    // userList 에서 클라이언트 삭제
    public void removeClientByName(RoomResponseDto room, String userUUID) {
        room.getUserList().remove(userUUID);
    }

    // 유저 카운터 return
    public boolean findUserCount(WebSocketMessage webSocketMessage){
        RoomResponseDto room = UserListMap.getInstance().getUserMap().get(webSocketMessage.getData());
        log.info("ROOM COUNT : [{} ::: {}]",room.toString(),room.getUserList().size());
        return room.getUserList().size() > 1;
    }
}