package kr.co.talk.domain.user.dto;

import lombok.*;

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
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStatusDto {
        private boolean isToday; // 오늘 status update 된적있는지
        private String name;
        private String nickname;
        private int statusEnergy;
        private int statusRelation;
        private int statusStress;
        private int statusStable;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserNameResponseDto {
        long userId;
        String name;
        String nickname;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminSearchUserIdResponseDto {
        String name;
        String nickname;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private Long userId;
        private String teamCode;
        private String nickname;
        private String userName;
        private String profileUrl;
        private String profileImgCode;
    }
}
