package kr.co.talk.user;

import kr.co.talk.domain.user.dto.RegisterAdminUserDto;
import kr.co.talk.domain.user.dto.RegisterUserDto;
import kr.co.talk.domain.user.dto.ResponseDto;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.TeamRepository;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.domain.user.service.SocialKakaoService;
import kr.co.talk.domain.user.service.UserService;
import kr.co.talk.global.config.jwt.JwtTokenProvider;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
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

    @Test
    @DisplayName("이미 ROLE_ADMIN으로 가입한 사용자는 ROLE_USER로 가입할 수 없음")
    void user_role_register() throws Exception {
        //given
        User adminUser = User.builder().userId(1L).userName("testName").userUid("abcdefi").nickname("nickName").build();
        User user = User.builder().userName("testName").userUid("abcdefi").nickname("nickName").build();

        RegisterAdminUserDto registerAdminUserDto = new RegisterAdminUserDto();
        registerAdminUserDto.setName(adminUser.getUserName());
        registerAdminUserDto.setTeamName("talk");
        ResponseDto.TeamCodeResponseDto teamCodeResponseDto = userService.registerAdminUser(registerAdminUserDto, 1L);

        RegisterUserDto registerUserDto = new RegisterUserDto();
        registerUserDto.setName(user.getUserName());
        registerUserDto.setTeamCode(teamCodeResponseDto.getTeamCode());
        userService.registerUser(registerUserDto, 1L);

        //when
        Throwable exception = assertThrows(CustomException.class, () -> {
            userService.registerUser(registerUserDto, 1L);
        });

        // then
        assertEquals(CustomError.ADMIN_CANNOT_REGISTER_USER, ((CustomException) exception).getCustomError());
    }
}
