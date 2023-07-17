package kr.co.talk.user;

import kr.co.talk.domain.user.dto.RegisterUserDto;
import kr.co.talk.domain.user.model.User;
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

import static kr.co.talk.domain.user.model.User.Role.ROLE_ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    SocialKakaoService socialKakaoService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("이미 ROLE_ADMIN 으로 가입한 사용자는 ROLE_USER 로 가입할 수 없음")
    void user_role_register() {
        //given
        User adminUser = userRepository.findByUserId(101L);
        assertEquals(adminUser.getRole(), ROLE_ADMIN);

        // when
        RegisterUserDto registerUserDto = RegisterUserDto.builder().name(adminUser.getUserName()).teamCode(adminUser.getTeam().getTeamCode()).build();

        // then
        CustomException exception = assertThrows(CustomException.class, () -> {
            userService.registerUser(registerUserDto, 101L);
        });
        assertEquals(exception.getCustomError(), CustomError.ADMIN_CANNOT_REGISTER_USER);
    }
}
