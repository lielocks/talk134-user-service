package kr.co.talk.domain.user.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.talk.domain.user.dto.QTeammateQueryDto;
import kr.co.talk.domain.user.dto.TeammateQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.talk.domain.user.model.QUser.user;

@RequiredArgsConstructor
@Repository
public class CustomUserRepository {
    private final JPAQueryFactory queryFactory;

    public List<TeammateQueryDto> selectTeammateByUserId(Long userId) {
        return queryFactory
                .select(new QTeammateQueryDto(
                        user.userName,
                        user.nickname,
                        user.profileImgCode,
                        user.userId))
                .from(user)
                .where(user.team.id.eq(
                        JPAExpressions
                                .select(user.team.id)
                                .from(user)
                                .where(user.userId.eq(userId))
                        )
                )
                .fetch();
    }
}
