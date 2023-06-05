package kr.co.talk.domain.user.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Data
@IdClass(NicknameMap.NicknameMapId.class)
@Entity
public class NicknameMap {

    @Id
    private int step;
    @Id
    private int sequence;

    private String value;

    private String code;

    @Builder
    @Data
    public static class NicknameMapId implements Serializable {
        private int step;
        private int sequence;
    }
}
