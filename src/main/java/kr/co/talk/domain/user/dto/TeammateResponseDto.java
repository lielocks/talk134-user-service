package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeammateResponseDto {
    private String nickname;
    private String name;
    private String profileUrl;
    private long userId;
}
