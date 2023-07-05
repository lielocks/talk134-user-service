package kr.co.talk.domain.user.service;

import kr.co.talk.domain.user.dto.AuthTokenDto;
import kr.co.talk.domain.user.dto.LoginDto;
import kr.co.talk.domain.user.dto.TestUserDto;
import kr.co.talk.domain.user.model.User;
import kr.co.talk.domain.user.repository.UserRepository;
import kr.co.talk.global.exception.CustomError;
import kr.co.talk.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;

import static kr.co.talk.domain.user.model.User.Role.ROLE_ADMIN;

@Slf4j
@RequiredArgsConstructor
@Service
public class TestLoginService {
    private final UserRepository userRepository;
    private final AuthService authService;

    private String generatePassword() {
        return "test_" + RandomStringUtils.randomAlphanumeric(12);
    }

    public TestUserDto signupUser() {
        String uid = generatePassword();
        User user = User.builder()
            .userUid(uid)
            .role(User.Role.ROLE_USER)
            .build();
        user = userRepository.save(user);
        return TestUserDto.builder()
            .id(user.getUserId())
            .password(uid)
            .build();
    }

    public TestUserDto signupAdmin() {
        String uid = generatePassword();
        User user = User.builder()
            .userUid(uid)
            .role(User.Role.ROLE_ADMIN)
            .build();
        user = userRepository.save(user);
        return TestUserDto.builder()
            .id(user.getUserId())
            .password(uid)
            .build();
    }

    public LoginDto login(TestUserDto request) {
        User user = userRepository.findById(request.getId())
            .orElseThrow(() -> new CustomException(CustomError.USER_DOES_NOT_EXIST));

        if (!user.getUserUid().equals(request.getPassword())) {
            throw new CustomException(CustomError.LOGOUT_FAILED);
        }

        AuthTokenDto authToken = authService.createAuthToken(user.getUserId());

        return LoginDto.builder()
            .userId(user.getUserId())
            .accessToken(authToken.getAccessToken())
            .refreshToken(authToken.getRefreshToken())
            .nickname(user.getNickname())
            .teamCode(user.getTeam() != null ? user.getTeam().getTeamCode() : null)
            .admin(user.getRole().equals(ROLE_ADMIN))
            .build();
    }
}
