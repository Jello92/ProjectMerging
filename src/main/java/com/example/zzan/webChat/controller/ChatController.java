package com.example.zzan.webChat.controller;

import com.example.zzan.webChat.dto.ChatRoomMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.ArrayList;
//클라이언트와 서버 간의 채팅 메시지를 처리하는 클래스
@Slf4j//클래스 내에서 로그를 기록할 때 사용
@RequiredArgsConstructor
@RestController
public class ChatController {
//    // 아래에서 사용되는 convertAndSend 를 사용하기 위해서 서언
//    // convertAndSend 는 객체를 인자로 넘겨주면 자동으로 Message 객체로 변환 후 도착지로 전송한다.
//    private final SimpMessageSendingOperations template;//SimpMessageSendingOperations는 메시지를 웹소켓 클라이언트에게 전송하기 위한 메소드를 제공하는 인터페이스
//    private final MsgChatService msgChatService;
//    private final ChatServiceMain chatServiceMain;
//    // 이 메소드는 클라이언트가 채팅방에 입장했을 때 호출되는 메소드로 웹소켓으로인해 "/chat/enterUser" 주제에 메시지가 도착하면 이 메소드가 호출(유저가 채팅방에 입장했다는 메시지를 전송하고, 해당 채팅방의 유저 수를 증가)
//    // MessageMapping 을 통해 webSocket 로 들어오는 메시지를 발신 처리한다.
//    // 이때 클라이언트에서는 /pub/chat/message 로 요청하게 되고 이것을 controller 가 받아서 처리한다.
//    // 처리가 완료되면 /sub/chat/room/roomId 로 메시지가 전송된다.
//    @MessageMapping("/chat/enterUser")//@MessageMapping는 웹소켓 클라이언트로부터 받은 메시지를 처리할 메소드를 정의하는 어노테이션으로 이 메소드는 클라이언트가 "/chat/enterUser" 주제로 메시지를 보낼 때 호출
//    public void enterUser(@Payload ChatDTO chat, SimpMessageHeaderAccessor headerAccessor) {
//        //@Payload는 메시지의 payload 부분을 인자로 받아오는 어노테이션으로 이 경우, 클라이언트가 보낸 메시지의 payload는 ChatDTO 객체로 변환되어 chat 인자에 전달
//        //SimpMessageHeaderAccessor은 STOMP 메시지의 헤더에 접근하기 위한 클래스로 이를 통해 세션 속성에 접근하거나, 메시지 헤더를 수정
//        // 채팅방 유저+1
//        chatServiceMain.plusUserCnt(chat.getRoomId());
//        // 채팅방에 유저 추가 및 UserUUID 반환
//        String userUUID = msgChatService.addUser(ChatRoomMap.getInstance().getChatRooms(), chat.getRoomId(), chat.getSender());
//        //userUUID는 '유저의 고유 식별자'를 의미//한 사용자가 채팅방에 들어가면 addUser 메소드를 통해 userUUID가 생성되고 이 값은 이후에 사용자가 채팅방에서 나갈 때 사용자를 찾아내는 데 사용
//        // 반환 결과를 socket session 에 userUUID 로 저장
//        headerAccessor.getSessionAttributes().put("userUUID", userUUID);
//        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
//        chat.setMessage(chat.getSender() + " 님 입장!!");
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
//    }
//    //채팅 메시지를 해당 채팅방에 전송//"/chat/sendMessage" 주제로 메시지를 보내면 이 메서드가 호출
//    //클라이언트가 채팅방에 메시지를 보낼 때 호출되는 메소드//
//    // 해당 유저
//    @MessageMapping("/chat/sendMessage")
//    public void sendMessage(@Payload ChatDTO chat) {
//        log.info("CHAT {}", chat);//로그 정보에 chat 정보를 기록
//        chat.setMessage(chat.getMessage());
//        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
//    }
//    //이 메소드는 유저가 채팅방에서 퇴장하거나 연결이 끊길 때 호출되는 이벤트 리스너
//    // 유저 퇴장 시에는 EventListener 을 통해서 유저 퇴장을 확인
//    @EventListener
//    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
//        log.info("DisConnEvent {}", event);
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());//StompHeaderAccessor를 이용하여 이벤트 메시지를 감싸고 있으며 이렇게 하면 STOMP 메시지의 헤더에 접근할 수 있음
//        // stomp 세션에 있던 uuid 와 roomId 를 확인해서 채팅방 유저 리스트와 room 에서 해당 유저를 삭제
//        String userUUID = (String) headerAccessor.getSessionAttributes().get("userUUID");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("roomId");
//        log.info("headAccessor {}", headerAccessor);
//        // 채팅방 유저 -1
//        chatServiceMain.minusUserCnt(roomId);
//        // 채팅방 유저 리스트에서 UUID 유저 닉네임 조회 및 리스트에서 유저 삭제
//        String username = msgChatService.findUserNameByRoomIdAndUserUUID(ChatRoomMap.getInstance().getChatRooms(), roomId, userUUID);//해당 유저의 닉네임을 찾음
//        msgChatService.delUser(ChatRoomMap.getInstance().getChatRooms(), roomId, userUUID);//해당 유저를 채팅방 유저 리스트에서 삭제
//        if (username != null) {
//            log.info("User Disconnected : " + username);
//            // builder 어노테이션 활용
//            ChatDTO chat = ChatDTO.builder()
//                    .type(ChatDTO.MessageType.LEAVE)
//                    .sender(username)
//                    .message(username + " 님 퇴장!!")
//                    .build();
//            template.convertAndSend("/sub/chat/room/" + roomId, chat);
//        }//if (username != null) 조건을 통해 유저 닉네임이 null이 아니라면, 해당 유저가 퇴장했다는 메시지를 생성하여 채팅방에 전송
//    }
//    // 채팅에 참여한 유저 리스트 반환
//    @GetMapping("/chat/userlist")
//    @ResponseBody
//    public ArrayList<String> userList(String roomId) {
//        return msgChatService.getUserList(ChatRoomMap.getInstance().getChatRooms(), roomId);
//    }
//    // 채팅에 참여한 유저 닉네임 중복 확인
//    @GetMapping("/chat/duplicateName")
//    @ResponseBody
//    public String isDuplicateName(@RequestParam("roomId") String roomId, @RequestParam("username") String username) {
//        // 유저 이름 확인
//        String userName = msgChatService.isDuplicateName(ChatRoomMap.getInstance().getChatRooms(), roomId, username);
//        log.info("동작확인 {}", userName);
//        return userName;
//    }
}