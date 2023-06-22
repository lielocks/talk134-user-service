package kr.co.talk.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.talk.domain.user.model.QNicknameMap.nicknameMap;

@RequiredArgsConstructor
@Repository
public class ProfileRepository {
    private final JPAQueryFactory queryFactory;

    public List<Integer> getNameCodeList(String profileImgCode) {
        String[] codes = profileImgCode.split("-");

        return queryFactory
                .select(nicknameMap.sequence)
                .from(nicknameMap)
                .where(nicknameMap.code.in(codes))
                .orderBy(nicknameMap.step.asc())
                .fetch();
    }
}
