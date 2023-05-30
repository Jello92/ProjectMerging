package com.example.zzan.room.service;

import com.example.zzan.global.dto.ResponseDto;
import com.example.zzan.global.exception.ApiException;
import com.example.zzan.global.util.BadWords;
import com.example.zzan.mypage.service.S3Uploader;
import com.example.zzan.room.dto.RoomRequestDto;
import com.example.zzan.room.dto.RoomResponseDto;
import com.example.zzan.room.entity.Category;
import com.example.zzan.room.entity.Room;
import com.example.zzan.room.entity.RoomHistory;
import com.example.zzan.room.repository.RoomHistoryRepository;
import com.example.zzan.room.repository.RoomRepository;
import com.example.zzan.user.entity.User;
import com.example.zzan.userHistory.entity.UserHistory;
import com.example.zzan.userHistory.repository.UserHistoryRepository;
import com.example.zzan.webChat.dto.ChatRoomMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.example.zzan.global.exception.ExceptionEnum.ROOM_NOT_FOUND;
import static com.example.zzan.global.exception.ExceptionEnum.UNAUTHORIZED_USER;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomHistoryRepository roomHistoryRepository;

    private final UserHistoryRepository userHistoryRepository;

    private final S3Uploader s3Uploader;


    @Transactional
    public ResponseDto<RoomResponseDto> createRoom(RoomRequestDto roomRequestDto, MultipartFile roomImage, User user) throws IOException {
        String roomImageUrl;

        roomImageUrl = s3Uploader.upload(roomImage, "images");

        if(roomImage == null){
                return ResponseDto.setBadRequest("이미지를 업로드해주세요.");
        }

        String roomTitle = roomRequestDto.getTitle();
        if(hasBadWord(roomTitle)){
            return ResponseDto.setBadRequest("방 제목에 사용할 수 없는 단어가 있습니다.");
        }

        Room room = new Room(roomRequestDto, user);

        if (!roomRequestDto.getIsPrivate()) {
        } else {
            String roomPassword = roomRequestDto.getRoomPassword();
            if (roomPassword == null || roomPassword.isEmpty())
                return ResponseDto.setBadRequest("방 비밀번호를 설정해주세요.");
        }
        room.setRoomImage(roomImageUrl);

        RoomResponseDto responseDto = new RoomResponseDto();

       responseDto.setUserList(new HashMap<String, WebSocketSession>());
        ChatRoomMap.getInstance().getChatRooms().put(String.valueOf(room.getId()), responseDto);

        RoomHistory roomHistory = new RoomHistory();
        roomHistory.setRoom(room);

        roomHistoryRepository.save(roomHistory);
        roomRepository.save(room);

        return ResponseDto.setSuccess("방을 생성하였습니다.", new RoomResponseDto(room));
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<RoomResponseDto>> getRooms(Pageable pageable) {
        List<RoomResponseDto> roomList = roomRepository.findAll(pageable).getContent().stream().map(RoomResponseDto::new).collect(Collectors.toList());
        return ResponseDto.setSuccess("전체 조회 성공", roomList);
    }

    @Transactional(readOnly = true)
    public ResponseDto<List<RoomResponseDto>> chooseCategory(Category category, Pageable pageable) {
        List<RoomResponseDto> roomList = roomRepository.findAllByCategory(category, pageable).stream().map(RoomResponseDto::new).collect(Collectors.toList());
        return ResponseDto.setSuccess("카테고리 검색 성공", roomList);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> updateRoom(Long roomId, RoomRequestDto roomRequestDto, MultipartFile roomImage, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );
        if (!room.getHostUser().getId().equals(user.getId())) {
            throw new ApiException(UNAUTHORIZED_USER);
        }

        room.update(roomRequestDto);

        if (roomImage != null && !roomImage.isEmpty()) {
            if (room.getRoomImage() != null) {
                s3Uploader.removeNewFile(new File(room.getRoomImage()));
            }

            try {
                String roomImageUrl = s3Uploader.upload(roomImage, "images");
                room.setRoomImage(roomImageUrl);
            } catch (IOException e) {
                return ResponseDto.setBadRequest("이미지를 업로드해주세요.");
            }
        }

        String roomTitle = roomRequestDto.getTitle();
        if(hasBadWord(roomTitle)){
            return ResponseDto.setBadRequest("방 제목에 사용할 수 없는 단어가 있습니다.");
        }


        roomRepository.save(room);
        return ResponseDto.setSuccess("방을 수정하였습니다.", null);
    }

    @Transactional
    public ResponseDto<RoomResponseDto> deleteRoom(Long roomId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new ApiException(ROOM_NOT_FOUND)
        );

        if (room.getHostUser().getId().equals(user.getId())) {
            roomRepository.delete(room);
            return ResponseDto.setSuccess("방을 삭제하였습니다.", null);
        } else {
            throw new ApiException(UNAUTHORIZED_USER);
        }
    }

    private boolean hasBadWord(String input){
        for(String badWord : BadWords.koreaWord1){
            if (input.contains(badWord)){
                return true;
            }
        }
        return false;
    }
    
    @Transactional
    public ResponseDto<RoomResponseDto> enterRoom(Model model, Long roomId, User user) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new ApiException(ROOM_NOT_FOUND));

        model.addAttribute("room", room);

        UserHistory userHistory = new UserHistory();
        userHistory.setHostUser(room.getHostUser()); // 방장 기록
        userHistory.setGuestUser(user); // 들어간 사람 기록

        RoomHistory roomHistory = new RoomHistory(room);
        userHistory.setRoom(roomHistory.getRoom());//방아이디

        model.addAttribute("uuid", UUID.randomUUID().toString());

        userHistoryRepository.save(userHistory);
        return ResponseDto.setSuccess("방에 입장하였습니다", new RoomResponseDto(room));
    }
}