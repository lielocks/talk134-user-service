package kr.co.talk.domain.user.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.talk.domain.user.dto.NicknameMapQueryDto;
import kr.co.talk.domain.user.dto.QNicknameMapQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kr.co.talk.domain.user.model.QNicknameMap.nicknameMap;

@RequiredArgsConstructor
@Repository
public class NicknameMapRepository {

    private final JPAQueryFactory queryFactory;

    @Transactional(readOnly = true)
    public List<NicknameMapQueryDto> getNicknameMap(List<Integer> nameCode) {
        return queryFactory
                .select(new QNicknameMapQueryDto(nicknameMap.code, nicknameMap.value))
                .from(nicknameMap)
                .where(linkConditions(nameCode))
                .orderBy(nicknameMap.step.asc())
                .fetch();
    }

    private BooleanExpression eqStepAndSequence(int step, int sequence) {
        return nicknameMap.step.eq(step).and(nicknameMap.sequence.eq(sequence));
    }

    private BooleanBuilder linkConditions(List<Integer> nameCode) {
        BooleanBuilder builder = new BooleanBuilder();
        for (int i = 0; i < nameCode.size(); i++) {
            builder.or(eqStepAndSequence(i + 1, nameCode.get(i)));
        }
        return builder;
    }
}
