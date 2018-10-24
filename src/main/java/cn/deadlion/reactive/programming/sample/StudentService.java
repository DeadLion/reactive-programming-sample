package cn.deadlion.reactive.programming.sample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author admin
 */
@Slf4j
@Service
public class StudentService {

    public String getStudentName() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(3000);
        log.info("getStudentName done,thread name:{},cost {} ms", Thread.currentThread(), System.currentTimeMillis() - start);
        return "Jasper";
    }


    public Integer getSutdentAge() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(2000);
        log.info("getSutdentAge done,thread name:{},cost {} ms", Thread.currentThread(), System.currentTimeMillis() - start);
        return 24;
    }

    public Map getSutdentFamilyInfo() throws InterruptedException {
        long start = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>(2);
        result.put("father's name", "John");
        result.put("mother's name", "Grace");
        Thread.sleep(1000);
        log.info("getSutdentFamilyInfo done,thread name:{},cost {} ms", Thread.currentThread(), System.currentTimeMillis() - start);
        return result;
    }
}
