package kr.co.talk.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDto {
    private String accessToken;
    private String refreshToken; // 쿠키로
    private Long userId;
    private String nickname;
    private String teamCode;
    private boolean isAdmin;
}
