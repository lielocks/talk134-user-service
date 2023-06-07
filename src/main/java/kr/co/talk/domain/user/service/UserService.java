package kr.co.talk.domain.user.service;

import java.util.*;
import java.util.stream.Collectors;

import kr.co.talk.domain.user.dto.*;
import kr.co.talk.domain.user.repository.CustomUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.talk.domain.user.dto.ResponseDto.CreateChatroomResponseDto;
import kr.co.talk.domain.user.dto.ResponseDto.TimeoutResponseDto;
import kr.co.talk.domain.user.dto.SocialKakaoDto;
import kr.co.talk.domain.user.model.Team;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.TeamRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static kr.co.talk.domain.user.model.User.Role.ROLE_ADMIN;
import static kr.co.talk.domain.user.model.User.Role.ROLE_USER;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final CustomUserRepository customUserRepository;

	@Transactional
	public User createUser(SocialKakaoDto.UserInfo userInfoDto) {
		User user = userInfoDto.createUser();
		return userRepository.save(user);
	}

	@Transactional
	public User createAdminUser(Long userId) {
		User user = User.builder().userId(userId).role(ROLE_ADMIN).build();
		return userRepository.save(user);
	}

	@Transactional
	public User createRoleUser(Long userId) {
		User user = User.builder().userId(userId).role(ROLE_USER).build();
		return userRepository.save(user);
	}

	public User findByUserUid(String userUid) {
		return userRepository.findByUserUid(userUid);
	}

	@Transactional
	public String saveTeam(RegisterAdminUserDto registerAdminUserDto) {
		String teamCode = makeCode();
		Team team = Team.builder().teamName(registerAdminUserDto.getTeamName()).teamCode(teamCode).build();
		teamRepository.save(team);
		log.info("TEAM = {}", team.getTeamName());
		return teamCode;
	}

	public ResponseDto.NameResponseDto nameFromUser(Long userId) {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
		} else {
			ResponseDto.NameResponseDto nameResponseDto = new ResponseDto.NameResponseDto();
			nameResponseDto.setName(user.getUserName());
			return nameResponseDto;
		}
	}

	public List<ResponseDto.UserIdResponseDto> searchUserId(String searchName) {
		List<User> users = userRepository.findUserByUserNameOrNickname(searchName);
		List<ResponseDto.UserIdResponseDto> resultList = new ArrayList<>();

		if (users == null || users.isEmpty()) {
			throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
		} else {
			for (User user : users) {
				ResponseDto.UserIdResponseDto userDto = new ResponseDto.UserIdResponseDto();
				userDto.setUserId(user.getUserId());
				userDto.setUserName(user.getUserName());
				resultList.add(userDto);
			}
		}

		return resultList;
	}

	public ResponseDto.TeamCodeResponseDto findTeamCode(Long userId) {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
		} else {
			ResponseDto.TeamCodeResponseDto teamCodeResponseDto = new ResponseDto.TeamCodeResponseDto();
			teamCodeResponseDto.setTeamCode(user.getTeam().getTeamCode());
			return teamCodeResponseDto;
		}
	}

	@Transactional
	public ResponseDto.TeamCodeResponseDto registerAdminUser(RegisterAdminUserDto registerAdminUserDto, Long userId) {
		String teamCode = saveTeam(registerAdminUserDto);
		Team team = teamRepository.findTeamByTeamCode(teamCode);

		Optional<User> existingUser = userRepository.findUserByRoleAndTeam(Arrays.asList(ROLE_ADMIN, ROLE_USER), team);
		if (existingUser.isPresent()) {
			throw new CustomException(CustomError.ADMIN_TEAM_ALREADY_EXISTS);
		}

		User user = userRepository.findByUserId(userId);
		if (user == null) {
			// 사용자 정보가 없을 경우, 새로운 사용자 생성
			user = createAdminUser(userId);
		} else if (user.getRole().equals(ROLE_ADMIN)) {
			throw new CustomException(CustomError.ADMIN_ALREADY_EXISTS);
		}

		user.registerInfo(registerAdminUserDto.getName(), team, ROLE_ADMIN);

		log.info("TEAM = {}", team.getTeamName());
		log.info("USER = {}", user.getRole());

		ResponseDto.TeamCodeResponseDto teamCodeResponseDto = new ResponseDto.TeamCodeResponseDto();
		teamCodeResponseDto.setTeamCode(teamCode);
		return teamCodeResponseDto;
	}

	private static String makeCode() {
		String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		StringBuilder codeBuilder = new StringBuilder();

		for (int i = 0; i < 6; i++) {
			int index = random.nextInt(characters.length());
			char randomChar = characters.charAt(index);
			codeBuilder.append(randomChar);
		}

		String code = codeBuilder.toString();
		log.info("code -> {}", code);
		return code;
	}

	@Transactional
	public ResponseDto.TeamCodeResponseDto registerUser(RegisterUserDto registerUserDto, Long userId) {
		Team team = teamRepository.findTeamByTeamCode(registerUserDto.getTeamCode());
		if (team == null) {
			throw new CustomException(CustomError.TEAM_CODE_NOT_FOUND);
		}
		log.info("TEAM = {}", team.getTeamName());
		log.info("TEAM = {}", team.getTeamCode());

		User user = userRepository.findByUserId(userId);

		if (user == null) {
			// 사용자 정보가 없을 경우, 새로운 사용자 생성
			user = createRoleUser(userId);
		} else {
			if (user.getRole().equals(ROLE_ADMIN)) {
				throw new CustomException(CustomError.ADMIN_CANNOT_REGISTER_USER);
			}
		}

		user.registerInfo(registerUserDto.getName(), team, ROLE_USER);
		ResponseDto.TeamCodeResponseDto teamCode = new ResponseDto.TeamCodeResponseDto();
		teamCode.setTeamCode(team.getTeamCode());
		return teamCode;
	}

	@Transactional
	public void updateTimeout(long userId, int timeout) {
		User user = userRepository.findByUserId(userId);
		if (user == null) {
			throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
		}
		
		Team team = user.getTeam();
		
		team.setTimeout(timeout);
	}

	public ResponseDto.TimeoutResponseDto getTimeout(long userId) {
	    User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }

        ResponseDto.TimeoutResponseDto timeoutResponseDto = new ResponseDto.TimeoutResponseDto();

        timeoutResponseDto.setTimeout(user.getTeam().getTimeout());

        return timeoutResponseDto;
	}

	public ResponseDto.CreateChatroomResponseDto requiredCreateChatroomInfo(long userId, List<Long> userList) {
        User createUser = userRepository.findByUserId(userId);
        if (createUser == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }
        int timeout = createUser.getTeam().getTimeout();
        String teamCode = createUser.getTeam().getTeamCode();
        StringBuilder sb = new StringBuilder();
        sb.append(createUser.getUserName());
        
        for (long u : userList) {
            User user = userRepository.findByUserId(u);
            if (user == null) {
                throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
            }

            sb.append(", ").append(user.getUserName());
        }

        CreateChatroomResponseDto chatroomResponseDto =
                ResponseDto.CreateChatroomResponseDto.builder()
                        .timeout(timeout)
                        .teamCode(teamCode)
                        .chatroomName(sb.toString())
                        .build();


        return chatroomResponseDto;
	}

	/**
	 * 참가자 조회 API
	 * @param userId user id
	 * @return {@link TeammateResponseDto} list
	 */
	@Transactional(readOnly = true)
	public List<TeammateResponseDto> getTeammates(Long userId) {
		List<TeammateQueryDto> list = customUserRepository.selectTeammateByUserId(userId);

		return list.stream()
				.map(query -> TeammateResponseDto.builder()
						.name(query.getName())
						.nickname(query.getNickname())
						.profileUrl(NicknameService.generateProfileUrl(query.getNickname()))
						.userId(query.getUserId())
						.build())
				.collect(Collectors.toUnmodifiableList());
	}
}