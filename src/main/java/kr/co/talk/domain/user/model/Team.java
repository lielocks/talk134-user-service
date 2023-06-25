package kr.co.talk.domain.user.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@ToString
public class Team {
    @Id
    @GeneratedValue
    private Long id;

    private String teamName;

    private String teamCode;
    private int timeout;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<User> users;

}
