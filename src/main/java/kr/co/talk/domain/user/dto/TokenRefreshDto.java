package kr.co.talk.domain.user.dto;

import lombok.*;

/**
 * 토큰 refresh 요청 시
 */
@Builder
@Data
public class TokenRefreshDto {
    private String accessToken;
}
