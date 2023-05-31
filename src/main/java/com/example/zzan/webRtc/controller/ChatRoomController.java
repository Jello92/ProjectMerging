//package com.example.zzan.webChat.controller;
//
//import com.example.zzan.webChat.dto.ChatRoomDto;
//import com.example.zzan.webChat.dto.ChatRoomMap;
//import com.example.zzan.webChat.service.ChatService.ChatServiceMain;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//
//import java.util.UUID;
//
//@Controller
//@RequiredArgsConstructor
//@Slf4j
//public class ChatRoomController {
//
// // ChatService Bean 가져오기
//// private final ChatServiceMain chatServiceMain;
////
//// // 채팅방 생성
//// // 채팅방 생성 후 다시 / 로 return
//// @PostMapping("/chat/createroom")
//// public String createRoom(@RequestParam("roomName") String name,
////                          @RequestParam("roomPwd") String roomPwd,
////                          @RequestParam("secretChk") String secretChk,
////                          @RequestParam(value = "maxUserCnt", defaultValue = "2") String maxUserCnt,
////                          @RequestParam("chatType") String chatType,
////                          RedirectAttributes rttr) {
////
////
////     // 매개변수 : 방 이름, 패스워드, 방 잠금 여부, 방 인원수
////     ChatRoomDto room;
////
////     room = chatServiceMain.createChatRoom(name, roomPwd, Boolean.parseBoolean(secretChk), Integer.parseInt(maxUserCnt), chatType);
////
////
////     log.info("CREATE Chat Room [{}]", room);
////
////     rttr.addFlashAttribute("roomName", room);
////     return "redirect:/";
//// }
////
////  //채팅방 입장 화면
//// // 파라미터로 넘어오는 roomId 를 확인후 해당 roomId 를 기준으로
//// // 채팅방을 찾아서 클라이언트를 chatroom 으로 보낸다.
//// @GetMapping("/chat/room")
//// public String roomDetail(Model model, String roomId){
////
////
////     ChatRoomDto room = ChatRoomMap.getInstance().getChatRooms().get(roomId);
////
////     model.addAttribute("room", room);
////
////
////     if (ChatRoomDto.ChatType.MSG.equals(room.getChatType())) {
////         return "chatroom";
////     }else{
////         model.addAttribute("uuid", UUID.randomUUID().toString());
////
////         return "rtcroom";
////     }
//// }
//
////  채팅방 비밀번호 확인
//// @PostMapping("/chat/confirmPwd/{roomId}")
//// @ResponseBody
//// public boolean confirmPwd(@PathVariable String roomId, @RequestParam String roomPwd){
////
////     // 넘어온 roomId 와 roomPwd 를 이용해서 비밀번호 찾기
////     // 찾아서 입력받은 roomPwd 와 room pwd 와 비교해서 맞으면 true, 아니면  false
////     return chatServiceMain.confirmPwd(roomId, roomPwd);
//// }
//
//
// // 유저 카운트
//// @GetMapping("/chat/chkUserCnt/{roomId}")
//// @ResponseBody
//// public boolean chUserCnt(@PathVariable String roomId){
////
////     return chatServiceMain.chkRoomUserCnt(roomId);
//// }
//}
