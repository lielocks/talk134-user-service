package kr.co.talk.user.service;

import kr.co.talk.domain.user.dto.RegisterAdminUserDto;
import kr.co.talk.domain.user.dto.RegisterUserDto;
import kr.co.talk.domain.user.model.Team;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.TeamRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.domain.user.service.AuthService;
import kr.co.talk.domain.user.service.SocialKakaoService;
import kr.co.talk.domain.user.service.UserService;
import kr.co.talk.global.config.jwt.JwtTokenProvider;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles(profiles = "test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    UserService userService;

    @Autowired
    SocialKakaoService socialKakaoService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    AuthService authService;

    @DisplayName("register method 가 있지만 transactional 이 걸려있기 때문에 repository 에 save")
    @BeforeEach
    void createMockAdminUser() {
        RegisterAdminUserDto mockAdminUserDto = RegisterAdminUserDto.builder().name("김이름").teamName("teamName").build();
        Team team = Team.builder().teamName(mockAdminUserDto.getTeamName()).teamCode("YnNme3").build();
        teamRepository.save(team);
        userRepository.save(User.builder().userName(mockAdminUserDto.getName()).team(team).role(User.Role.ROLE_ADMIN).build());
    }

    @Test
    @Transactional
    @DisplayName("이미 ROLE_ADMIN 으로 가입한 사용자는 ROLE_USER 로 가입할 수 없음 ADMIN_CANNOT_REGISTER_USER exception 발생")
    void user_role_register() {
        //given
        RegisterUserDto registerUserDto =
                RegisterUserDto.builder().name("김이름").teamCode("YnNme3").build();

        // when
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser(registerUserDto, userRepository.findUserByUserNameOrNickname("김이름").get(0).getUserId());
        });

        // then
        assertEquals(exception.getCustomError(), CustomError.ADMIN_CANNOT_REGISTER_USER);
    }
}
