package com.example.zzan.webRtc.dto;

import com.example.zzan.room.dto.RoomResponseDto;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

// 싱글톤으로 생성
// 모든 ChatService 에서 ChatRooms가 공통된 필요함으로
@Getter
@Setter
public class UserListMap {
    private static UserListMap userListMap = new UserListMap();
    private Map<Long, RoomResponseDto> userMap = new LinkedHashMap<>();

//    @PostConstruct
//    private void init() {
//        chatRooms = new LinkedHashMap<>();
//    }

    private UserListMap(){}

    public static UserListMap getInstance(){
        return userListMap;
    }

}
