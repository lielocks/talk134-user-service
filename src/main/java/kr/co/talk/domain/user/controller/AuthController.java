package kr.co.talk.domain.user.controller;

import kr.co.talk.domain.user.dto.LoginDto;
import kr.co.talk.domain.user.dto.SocialKakaoDto;
import kr.co.talk.domain.user.dto.TestUserDto;
import kr.co.talk.domain.user.service.AuthService;
import kr.co.talk.domain.user.service.SocialKakaoService;
import kr.co.talk.domain.user.service.TestLoginService;
import kr.co.talk.global.constant.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final SocialKakaoService socialKakaoService;
    private final TestLoginService testLoginService;

    //카카오 로그인
    @PostMapping("/login/kakao")
    public LoginDto loginKakao(@RequestBody SocialKakaoDto.TokenRequest request, HttpServletResponse response) throws Exception {
        LoginDto dto = socialKakaoService.login(request);
        log.info("User logged in: {}", dto.getUserId());
        ResponseCookie cookie = ResponseCookie.from(Constants.REFRESH_TOKEN_COOKIE_NAME, dto.getRefreshToken())
                .maxAge(Constants.REFRESH_TOKEN_TTL_SECONDS)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return dto;
    }

    //테스트용 일반 계정 생성
    @PostMapping("/test/user")
    public TestUserDto testSignup() {
        return testLoginService.signupUser();
    }

    //테스트용 어드민 계정 생성
    @PostMapping("/test/admin")
    public TestUserDto testSignupAdmin() {
        return testLoginService.signupAdmin();
    }

    //테스트용 로그인
    @PostMapping("/login/test")
    public LoginDto loginTest(@RequestBody TestUserDto request, HttpServletResponse response) {
        LoginDto dto = testLoginService.login(request);
        log.info("User logged in: {}", dto.getUserId());
        ResponseCookie cookie = ResponseCookie.from(Constants.REFRESH_TOKEN_COOKIE_NAME, dto.getRefreshToken())
            .maxAge(Constants.REFRESH_TOKEN_TTL_SECONDS)
            .path("/")
            .sameSite("None")
            .secure(true)
            .httpOnly(true)
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return dto;
    }

    //토큰 재발급
    @PostMapping("/refresh")
    public LoginDto tokenRefresh(
            @CookieValue(Constants.REFRESH_TOKEN_COOKIE_NAME) Cookie refreshTokenCookie,
            HttpServletResponse response) {
        var token = authService.tokenRefresh(refreshTokenCookie.getValue());
        ResponseCookie cookie = ResponseCookie.from(Constants.REFRESH_TOKEN_COOKIE_NAME, token.getRefreshToken())
                .maxAge(Constants.REFRESH_TOKEN_TTL_SECONDS)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return token;
    }

}
