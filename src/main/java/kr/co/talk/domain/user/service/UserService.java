package kr.co.talk.domain.user.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

import kr.co.talk.domain.user.dto.ResponseDto.ChatRoomEnterResponseDto;
import kr.co.talk.domain.user.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.talk.domain.user.dto.RegisterAdminUserDto;
import kr.co.talk.domain.user.dto.RegisterUserDto;
import kr.co.talk.domain.user.dto.ResponseDto;
import kr.co.talk.domain.user.dto.*;
import kr.co.talk.domain.user.dto.ResponseDto.CreateChatroomResponseDto;
import kr.co.talk.domain.user.dto.ResponseDto.TeamCodeResponseDto;
import kr.co.talk.domain.user.dto.ResponseDto.UserNameResponseDto;
import kr.co.talk.domain.user.model.Team;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.stream.Collectors;

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
    private final AuthTokenRepository authTokenRepository;

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
        Team team = Team.builder().teamName(registerAdminUserDto.getTeamName()).teamCode(teamCode)
                .build();
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

    public List<ResponseDto.UserIdResponseDto> searchUserId(String teamCode, String searchName) {
        List<User> users = userRepository.findUserByUserNameOrNickname(searchName).stream()
                .filter(u -> u.getTeam().getTeamCode().equals(teamCode))
                .collect(Collectors.toList());
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

    public ResponseDto.FindChatroomResponseDto findChatroomInfo(Long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        } else {
            ResponseDto.FindChatroomResponseDto chatroomResponseDto =
                    new ResponseDto.FindChatroomResponseDto();
            chatroomResponseDto.setTeamCode(user.getTeam().getTeamCode());
            chatroomResponseDto.setUserRole(user.getRole().toString());
            return chatroomResponseDto;
        }
    }

    @Transactional
    public ResponseDto.TeamCodeResponseDto registerAdminUser(RegisterAdminUserDto registerAdminUserDto, Long userId) {
        Optional<Team> teamByTeamName = teamRepository.findTeamByTeamName(registerAdminUserDto.getTeamName());
        if (teamByTeamName.isPresent()) {
            throw new CustomException(CustomError.ADMIN_TEAM_ALREADY_EXISTS);
        }
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
    public ResponseDto.TeamCodeResponseDto registerUser(RegisterUserDto registerUserDto,
                                                        Long userId) {
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
    
    public ResponseDto.UserStatusDto getUserStatus(long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }

        if (user.getStatusChangeTime()!=null && isToday(user.getStatusChangeTime())) {
            // status가 오늘 업데이트가 한번이라도 되었으면 기존 값 리턴
            return ResponseDto.UserStatusDto.builder()
                    .isToday(true)
                    .name(user.getUserName())
                    .nickname(user.getNickname())
                    .statusEnergy(user.getStatusEnergy())
                    .statusRelation(user.getStatusRelation())
                    .statusStable(user.getStatusStable())
                    .statusStress(user.getStatusStress())
                    .build();
        }

        return ResponseDto.UserStatusDto.builder()
                .name(user.getUserName())
                .nickname(user.getNickname())
                .build();
    }

    public boolean isToday(Timestamp currentTime) {
        Date currentDate = new Date(currentTime.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = sdf.format(currentDate);

        Date today = new Date();
        String todayDateString = sdf.format(today);

        return currentDateString.equals(todayDateString);
    }

    public ResponseDto.CreateChatroomResponseDto requiredCreateChatroomInfo(long userId,
                                                                            List<Long> userList) {
        User createUser = userRepository.findByUserId(userId);
        if (createUser == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }
        int timeout = createUser.getTeam().getTimeout();
        String teamCode = createUser.getTeam().getTeamCode();
        StringBuilder sb = new StringBuilder();

        for (long u : userList) {
            User user = userRepository.findByUserId(u);
            if (user == null) {
                throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
            }

            if (sb.length() == 0) {
                sb.append(user.getUserName());
            } else {
                sb.append(", ").append(user.getUserName());
            }
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
     *
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
                        .profileUrl(NicknameService.generateProfileUrl(query.getProfileImgCode()))
                        .userId(query.getUserId())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ChatRoomEnterResponseDto> requiredEnterInfo(List<Long> userList) {
        List<EnterUserQueryDto> list = customUserRepository.selectEnterUserInfo(userList);
        if (list.size() < 1) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }

                return list.stream()
                        .map(dto -> ChatRoomEnterResponseDto.builder()
                                .userId(dto.getUserId())
                                .userName(dto.getName())
                                .nickname(dto.getNickname())
                                .profileUrl(NicknameService.generateProfileUrl(dto.getProfileImgCode()))
                                .build())
                        .collect(Collectors.toList());
    }
    
    
    /**
     * user의 status값 update
     * 
     * @param userId
     * @param updateRequestStatusDto
     */
    @Transactional
    public void changeStatus(long userId,  ResponseDto.UserStatusDto updateRequestStatusDto) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }
        
        user.setStatus(updateRequestStatusDto);
}

    public String sendUserImgCode(long userId) {
        User user = userRepository.findByUserId(userId);
        String profileImgCode = user.getProfileImgCode();
        return profileImgCode.substring(profileImgCode.length() - 2);
    }
    
    public TeamCodeResponseDto findTeamCode(long userId) {
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
        }
        TeamCodeResponseDto codeResponseDto = new TeamCodeResponseDto();
        codeResponseDto.setTeamCode(user.getTeam().getTeamCode());
        return codeResponseDto;
    }
    
    public List<UserNameResponseDto> userNameNickname(List<Long> userIds) {
        List<UserNameResponseDto> list = new ArrayList<>();

        userIds.forEach(uid -> {
            User user = userRepository.findByUserId(uid);
            if (user == null) {
                throw new CustomException(CustomError.USER_DOES_NOT_EXIST);
            }
            
            list.add(UserNameResponseDto.builder()
                    .userId(uid)
                    .name(user.getUserName())
                    .nickname(user.getNickname())
                    .build());
        });
        
        return list;
        
    }

    /**
     * redis에 있는 리프레쉬 토큰을 삭제해준다. 재발급을 막기 위함.
     * @param userId user id
     */
    public void logout(long userId) {
        authTokenRepository.deleteByUserId(userId);
    }
}