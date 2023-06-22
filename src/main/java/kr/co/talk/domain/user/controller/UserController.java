package kr.co.talk.domain.user.controller;

import kr.co.talk.domain.user.dto.*;
import kr.co.talk.domain.user.dto.ResponseDto.ChatRoomEnterResponseDto;
import kr.co.talk.domain.user.dto.ResponseDto.TeamCodeResponseDto;
import kr.co.talk.domain.user.dto.ResponseDto.UserNameResponseDto;
import kr.co.talk.domain.user.service.NicknameService;
import kr.co.talk.domain.user.service.ProfileService;
import kr.co.talk.domain.user.service.UserService;
import kr.co.talk.global.aspect.UserId;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final NicknameService nicknameService;
    private final ProfileService profileService;

    @PostMapping("/admin/register")
    public ResponseDto.TeamCodeResponseDto registerAdminUser(
            @RequestBody RegisterAdminUserDto registerAdminUserDto,
            @RequestHeader(value = "userId") Long userId) {
        log.info("Received userId from header: {}", userId);
        return userService.registerAdminUser(registerAdminUserDto, userId);
    }

    @PostMapping("/register")
    public ResponseDto.TeamCodeResponseDto registerUser(
            @RequestBody RegisterUserDto registerUserDto,
            @RequestHeader(value = "userId") Long userId) {
        return userService.registerUser(registerUserDto, userId);
    }

    @GetMapping("/name")
    public ResponseDto.NameResponseDto findUserName(@RequestHeader(value = "userId") Long userId) {
        return userService.nameFromUser(userId);
    }

    @GetMapping("/findChatroomInfo/{userId}")
    public ResponseDto.FindChatroomResponseDto findChatroomInfo(@PathVariable("userId") Long userId) {
        return userService.findChatroomInfo(userId);
    }

    @GetMapping("/id/{teamCode}/{searchName}")
    public List<ResponseDto.UserIdResponseDto> getUserIdByName(
            @PathVariable("teamCode") String teamCode,
            @PathVariable("searchName") String searchName) {
        return userService.searchUserId(teamCode, searchName);
    }

    /**
     * 대화방 timeout 설정 변경
     * 
     * @param timeout
     * @return
     */
    @PutMapping("/update-timeout/{timeOut}")
    public ResponseEntity<?> updateTimeout(@RequestHeader(value = "userId") Long userId,
            @PathVariable(name = "timeOut") int timeout) {
        userService.updateTimeout(userId, timeout);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-timeout")
    public ResponseDto.TimeoutResponseDto getTimeout(@RequestHeader(value = "userId") Long userId) {
        return userService.getTimeout(userId);
    }

    /**
     * user의 status값 리턴
     * chat-service에서 호출 
     * 
     * @param userId
     * @return
     */
    @GetMapping("/status/{userId}")
    public ResponseDto.UserStatusDto getUserStaus(@PathVariable("userId") long userId) {
        return userService.getUserStatus(userId);
    }

    /**
     * chat-service에서 호출하는 api create chatroom시 필요한 dto response
     * 
     * @param userId : chatroom 생성자, userList: 생성자가 고른 userIds
     * @return
     */
    @PostMapping("/required-create-chatroom-info/{userId}")
    public ResponseEntity<?> getTimeout(@PathVariable(value = "userId") long userId,
            @RequestBody List<Long> userList) {
        return ResponseEntity.ok(userService.requiredCreateChatroomInfo(userId, userList));
    }

    @RequestMapping(value = "/nickname", method = {RequestMethod.POST, RequestMethod.PUT})
    public NicknameResponseDto createNickname(@RequestHeader(value = "userId") Long userId,
            @RequestBody NicknameRequestDto requestDto) {
        if (requestDto.getNameCode().size() != 3) {
            throw new CustomException(CustomError.NAMECODE_SIZE_NOT_3);
        }
        return nicknameService.getNicknameAndSave(userId, requestDto.getNameCode());
    }

    @GetMapping("/teammate")
    public List<TeammateResponseDto> getTeammates(@RequestHeader(value = "userId") Long userId) {
        if (userId == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }
        return userService.getTeammates(userId);
    }

    
    /**
     * chat service에서 필수형 feedback update request
     * 
     * @param userId
     * @param updateRequestStatusDto
     * @return
     */
    @PutMapping("/changeStatus/{userId}")
    public ResponseEntity<?> changeStatus(@PathVariable("userId") long userId, @RequestBody ResponseDto.UserStatusDto updateRequestStatusDto) {
        userService.changeStatus(userId, updateRequestStatusDto);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/findTeamCode/{userId}")
    public TeamCodeResponseDto findTeamCode(@PathVariable("userId") long userId) {
        return userService.findTeamCode(userId);
    }

	@UserId
	@GetMapping("/enter-info/{userList}")
	public List<ChatRoomEnterResponseDto> findEnterInfo(@RequestHeader(value = "userId") long userId, @PathVariable List<Long> userList) {
		return userService.requiredEnterInfo(userList);
	}

	@GetMapping("/img-code")
	public String findEnterInfo(@RequestHeader(value = "userId") long userId) {
		return userService.sendUserImgCode(userId);
	}
	
	@GetMapping("/userName/nickname/{userIds}")
	public List<UserNameResponseDto> userNameNickname(@PathVariable List<Long> userIds){
	    return userService.userNameNickname(userIds);
	}

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "userId") long userId) {
        userService.logout(userId);
    }

    @GetMapping("/profile")
    public UserProfileDto profile(@RequestHeader(value = "userId") long userId) {
        return profileService.getProfile(userId);
    }
}
