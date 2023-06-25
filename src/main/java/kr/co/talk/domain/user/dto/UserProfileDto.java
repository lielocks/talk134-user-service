package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class UserProfileDto {
    private String name;
    private String nickname;
    private String profileUrl;
    private List<Integer> nameCode;
}
