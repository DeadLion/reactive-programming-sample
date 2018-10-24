package cn.deadlion;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalField;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void date(){
        Instant now = Instant.now();
        ZonedDateTime atZone = now.atZone(ZoneOffset.UTC);
        ZonedDateTime with = atZone.with(TemporalAdjusters.firstDayOfMonth());
        System.out.println(with.getYear());
        System.out.println(with.getMonth());
    }
}
