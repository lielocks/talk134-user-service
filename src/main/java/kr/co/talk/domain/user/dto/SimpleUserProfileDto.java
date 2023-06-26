package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SimpleUserProfileDto {
    private long userId;
    private String nickname;
    private String profileUrl;
}
