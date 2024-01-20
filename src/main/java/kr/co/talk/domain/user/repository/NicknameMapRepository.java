package kr.co.talk.domain.user.repository;

import kr.co.talk.domain.user.model.NicknameMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NicknameMapRepository extends JpaRepository<NicknameMap, Integer> {
}
