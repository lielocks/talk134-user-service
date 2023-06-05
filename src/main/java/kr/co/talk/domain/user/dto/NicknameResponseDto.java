package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NicknameResponseDto {
    private String nickname;
    private String profileUrl;
}
