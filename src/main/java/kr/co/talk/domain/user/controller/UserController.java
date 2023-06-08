package kr.co.talk.domain.user.controller;

import kr.co.talk.domain.user.dto.*;
import kr.co.talk.domain.user.dto.ResponseDto.ChatRoomEnterResponseDto;
import kr.co.talk.domain.user.service.NicknameService;
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

	@ PostMapping("/admin/register")
	public ResponseDto.TeamCodeResponseDto registerAdminUser(@RequestBody RegisterAdminUserDto registerAdminUserDto,
			@RequestHeader(value = "userId") Long userId) {
		log.info("Received userId from header: {}", userId);
		return userService.registerAdminUser(registerAdminUserDto, userId);
	}

	@PostMapping("/register")
	public ResponseDto.TeamCodeResponseDto registerUser(@RequestBody RegisterUserDto registerUserDto,
			@RequestHeader(value = "userId") Long userId) {
		return userService.registerUser(registerUserDto, userId);
	}

	@GetMapping("/name")
	public ResponseDto.NameResponseDto findUserName(@RequestHeader(value = "userId") Long userId) {
		return userService.nameFromUser(userId);
	}

	@GetMapping("/teamCode/{userId}")
	public ResponseDto.TeamCodeResponseDto getUserTeamCode(@PathVariable("userId") Long userId) {
		return userService.findTeamCode(userId);
	}

	@GetMapping("/id/{teamCode}/{searchName}")
	public List<ResponseDto.UserIdResponseDto> getUserIdByName(@PathVariable("teamCode") String teamCode, @PathVariable("searchName") String searchName) {
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
	 * chat-service에서 호출하는 api
	 * create chatroom시 필요한 dto response
	 * 
	 * @param userId : chatroom 생성자, userList: 생성자가 고른 userIds
	 * @return
	 */
	@PostMapping("/required-create-chatroom-info/{userId}")
	public ResponseEntity<?> getTimeout(@PathVariable(value = "userId") long userId,  @RequestBody List<Long> userList) {
        return ResponseEntity.ok(userService.requiredCreateChatroomInfo(userId, userList));
    }

	@RequestMapping(value = "/nickname", method = {RequestMethod.POST, RequestMethod.PUT})
	public NicknameResponseDto createNickname(@RequestHeader(value = "userId") Long userId, @RequestBody NicknameRequestDto requestDto) {
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

	@UserId
	@GetMapping("/enter-info")
	public ChatRoomEnterResponseDto findEnterInfo(@RequestHeader(value = "userId") long userId) {
		return userService.requiredEnterInfo(userId);
	}
}
