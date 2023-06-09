package kr.co.talk.domain.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class ResponseDto {

    @Data
    public static class NameResponseDto {
        private String name;
    }

    @Data
    public static class TeamCodeResponseDto {
        private String teamCode;
    }

    @Data
    public static class TimeoutResponseDto {
        private int timeout;
    }

    @Data
    public static class UserIdResponseDto {
        private Long userId;
        private String userName;
    }

    /**
     * chat-service에서 chatroom create시 필요한 dto
     */
    @Data
    @Builder
    public static class CreateChatroomResponseDto {
        private int timeout;
        private String teamCode;
        private String chatroomName;
    }

    @Data
    @Builder
    public static class ChatRoomEnterResponseDto {
        private String nickname;
        private String userName;
        private String profileUrl;
    }
}
