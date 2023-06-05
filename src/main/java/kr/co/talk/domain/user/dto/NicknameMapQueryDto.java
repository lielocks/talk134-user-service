package kr.co.talk.domain.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

@Data
public class NicknameMapQueryDto {
    private String code;
    private String value;

    @QueryProjection
    public NicknameMapQueryDto(String code, String value) {
        this.code = code;
        this.value = value;
    }
}
