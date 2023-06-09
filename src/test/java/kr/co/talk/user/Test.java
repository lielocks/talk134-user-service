package kr.co.talk.user;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    @org.junit.jupiter.api.Test
    void test() {
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        Date currentDate = new Date(currentTime.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateString = sdf.format(currentDate);

        Date today = new Date();
        String todayDateString = sdf.format(today);

        boolean isToday = currentDateString.equals(todayDateString);
        
        System.out.println(isToday);
    }
}
