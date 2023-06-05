package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 토큰 refresh 요청 시
 */
@Builder
@Getter
public class TokenRefreshDto {
    private String accessToken;
}
