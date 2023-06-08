package kr.co.talk.domain.user.repository;

import kr.co.talk.domain.user.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findTeamByTeamCode(String teamCode);

    Optional<Team> findTeamByTeamName(String teamName);
}
