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
    public static class FindChatroomResponseDto {
        private String teamCode;
        private String userRole;
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
        private Long userId;
        private String nickname;
        private String userName;
        private String profileUrl;
    }

    @Data
    @Builder
    public static class UserStatusDto {
        private boolean isToday; // 오늘 status update 된적있는지
        private String name;
        private String nickname;
        private int statusEnergy;
        private int statusRelation;
        private int statusStress;
        private int statusStable;
    }

}
