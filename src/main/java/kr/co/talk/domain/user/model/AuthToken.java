package kr.co.talk.domain.user.model;

import kr.co.talk.global.constant.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

/**
 * redis 저장 객체
 */
@RedisHash(value = "authToken", timeToLive = Constants.REFRESH_TOKEN_TTL_SECONDS)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthToken {

    @Id
    private String refreshToken;

    //userId로 객체 조회
    @Indexed
    private Long userId;

}
