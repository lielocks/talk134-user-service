package kr.co.talk.domain.user.controller;

import kr.co.talk.domain.user.dto.*;
import kr.co.talk.domain.user.service.NicknameService;
import kr.co.talk.domain.user.service.UserService;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
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
	private final NicknameService nicknameService;

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

	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/nickname")
	public NicknameResponseDto createNickname(@RequestHeader(value = "userId") Long userId, @RequestBody NicknameRequestDto requestDto) {
		if (requestDto.getNameCode().size() != 3) {
			throw new CustomException(CustomError.NAMECODE_SIZE_NOT_3);
		}
		var result = nicknameService.getNickname(requestDto.getNameCode());
		userService.updateNickname(userId, result.getNickname());

		return result;
	}

}
