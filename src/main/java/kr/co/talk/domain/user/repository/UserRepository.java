package kr.co.talk.domain.user.repository;

import kr.co.talk.domain.user.model.Team;
import kr.co.talk.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByUserUid(String userUid);
    User findByUserId(Long id);

    @Query("SELECT u FROM User u WHERE u.role IN :roles AND u.team = :team")
    Optional<User> findUserByRoleAndTeam(@Param("roles") List<User.Role> roles, @Param("team") Team team);

    @Query("SELECT u FROM User u WHERE u.userName = :searchName OR u.nickname = :searchName")
    List<User> findUserByUserNameOrNickname(@Param("searchName") String searchName);
    
    List<User> findUserByTeamId(long teamId);

    List<User> findUsersByUserIdIn(Set<Long> userIds);
}
