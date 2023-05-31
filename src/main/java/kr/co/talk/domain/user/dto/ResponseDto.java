package kr.co.talk.domain.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class ResponseDto {

    @Data
    public static class NameResponseDto{
        private String name;
    }

    @Data
    public static class TeamCodeResponseDto{
        private String teamCode;
    }

    @Data
    public static class UserIdResponseDto {
        private Long userId;
        private String userName;
    }
}
