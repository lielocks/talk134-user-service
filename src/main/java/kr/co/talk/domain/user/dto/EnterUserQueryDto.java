package kr.co.talk.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class EnterUserQueryDto {
    private String name;
    private String nickname;
    private String profileImgCode;

    @QueryProjection
    public EnterUserQueryDto(String name, String nickname, String profileImgCode) {
        this.name = name;
        this.nickname = nickname;
        this.profileImgCode = profileImgCode;
    }

}
