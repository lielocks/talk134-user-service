package kr.co.talk.global.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh-token";

    public static final int ACCESS_TOKEN_TTL_SECONDS = 60 * 30;
    public static final int REFRESH_TOKEN_TTL_SECONDS = 60 * 60 * 24 * 15;
}
