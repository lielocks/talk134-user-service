package kr.co.talk.domain.user.controller;

import kr.co.talk.domain.user.dto.RegisterAdminUserDto;
import kr.co.talk.domain.user.dto.RegisterUserDto;
import kr.co.talk.domain.user.dto.ResponseDto;
import kr.co.talk.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/admin/register")
	public ResponseDto.TeamCodeResponseDto registerAdminUser(@RequestBody RegisterAdminUserDto registerAdminUserDto,
			@RequestHeader(value = "userId") Long userId) {
		return userService.registerAdminUser(registerAdminUserDto, userId);
	}

	@PostMapping("/register")
	public ResponseEntity registerUser(@RequestBody RegisterUserDto registerUserDto,
			@RequestHeader(value = "userId") Long userId) {
		userService.registerUser(registerUserDto, userId);
		return new ResponseEntity(HttpStatus.OK);
	}

	@GetMapping("/name")
	public ResponseDto.NameResponseDto findUserName(@RequestHeader(value = "userId") Long userId) {
		return userService.nameFromUser(userId);
	}

	@GetMapping("/teamCode/{userId}")
	public ResponseDto.TeamCodeResponseDto getUserTeamCode(@PathVariable("userId") Long userId) {
		return userService.findTeamCode(userId);
	}

	@GetMapping("/id/{searchName}")
	public List<ResponseDto.UserIdResponseDto> getUserIdByName(@PathVariable("searchName") String searchName) {
		return userService.searchUserId(searchName);
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

}
