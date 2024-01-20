package kr.co.talk.domain.user.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Data
@IdClass(NicknameMap.NicknameMapId.class)
@Entity
public class NicknameMap {

    @Id
    private int step;
    @Id
    private int sequence;

    @Column(name = "nickname_value")
    private String value;

    private String code;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class NicknameMapId implements Serializable {
        private int step;
        private int sequence;
    }
}
