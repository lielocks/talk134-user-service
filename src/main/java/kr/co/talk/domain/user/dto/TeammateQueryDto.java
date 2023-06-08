package kr.co.talk.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class TeammateQueryDto {
    private String name;
    private String nickname;
    private String profileImgCode;
    private Long userId;

    @QueryProjection
    public TeammateQueryDto(String name, String nickname, String profileImgCode, Long userId) {
        this.name = name;
        this.nickname = nickname;
        this.profileImgCode = profileImgCode;
        this.userId = userId;
    }
}
