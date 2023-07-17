package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterAdminUserDto {
    private String name;
    private String teamName;
}
