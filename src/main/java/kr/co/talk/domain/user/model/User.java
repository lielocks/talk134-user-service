package kr.co.talk.domain.user.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import kr.co.talk.domain.user.dto.ResponseDto;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.sql.Timestamp;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@ToString
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    //소셜 로그인시 유저 정보키
    //@Column(nullable = false)
    private String userUid;

    @Pattern(regexp = "^[가-힣]{2,}$", message = "이름은 한글 2자 이상이어야 합니다.")
    private String userName;

    @Setter
    @Column(length = 20)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    private int statusEnergy;

    private int statusRelation;

    private int statusStress;

    private int statusStable;

    @Setter
    private String profileImgCode;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createTime;

    private Timestamp statusChangeTime;
    
    public void setStatus(ResponseDto.UserStatusDto statusDto) {
        this.statusEnergy = statusDto.getStatusEnergy();
        this.statusRelation = statusDto.getStatusRelation();
        this.statusStress = statusDto.getStatusStress();
        this.statusStable = statusDto.getStatusStable();
        this.statusChangeTime =  new Timestamp(System.currentTimeMillis());
    }
    

    public enum Role {
        ROLE_USER, ROLE_ADMIN
    }

    public void registerInfo(String userName, Team team, Role role) {
        this.userName = userName;
        this.team = team;
        this.role = role;
    }

}
