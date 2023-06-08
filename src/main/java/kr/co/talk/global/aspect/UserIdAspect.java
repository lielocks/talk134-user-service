package kr.co.talk.global.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Aspect
@Component
@RequiredArgsConstructor
public class UserIdAspect {

    private final HttpServletRequest request;

    private final HttpServletResponse response;

    @AfterReturning(
            pointcut = "@annotation(userIdAnnotation)",
            returning = "returnValue"
    )
    public void addUserIdToResponseHeader(
            JoinPoint joinPoint,
            Object returnValue,
            UserId userIdAnnotation
    ) {
        Long userId = getUserIdFromHeader(request);
        response.setHeader("userId", String.valueOf(userId));
    }

    private Long getUserIdFromHeader(HttpServletRequest request) {
        return Long.valueOf(request.getHeader("userId"));
    }
}
