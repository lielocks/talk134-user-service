package kr.co.talk.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LoginDto {
    private String accessToken;

    @JsonIgnore
    private String refreshToken; // 쿠키로

    private Long userId;
    private String nickname;
    private String teamCode;

    @JsonProperty(value = "isAdmin")
    private boolean admin;
}
