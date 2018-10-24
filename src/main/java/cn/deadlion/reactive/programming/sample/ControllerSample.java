package cn.deadlion.reactive.programming.sample;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author admin
 */
@RestController
public class ControllerSample {

    ExecutorService es = Executors.newCachedThreadPool();

    @Autowired
    private StudentService studentService;

    @RequestMapping("/getStudentInfo")
    public Object getStudentInfo() {
        long start = System.currentTimeMillis();
        Map<String, Object> resultMap = new HashMap<>(10);

        try {
            resultMap.put("studentName", studentService.getStudentName());
            resultMap.put("studentAge", studentService.getSutdentAge());
            resultMap.put("studentFamilyInfo", studentService.getSutdentFamilyInfo());
        } catch (Exception e) {
            resultMap.put("errMsg", e.getMessage());
        }
        resultMap.put("total cost", System.currentTimeMillis() - start);
        return resultMap;
    }


    @RequestMapping("/getStudentInfoWithFuture")
    public Object getStudentInfoWithFuture() {
        long start = System.currentTimeMillis();
        Map<String, Object> resultMap = new HashMap<>(10);

        try {
            CountDownLatch countDownLatch = new CountDownLatch(3);

            Future futureStudentName = es.submit(() -> {
                Object studentName = studentService.getStudentName();
                countDownLatch.countDown();
                return studentName;
            });

            Future futureStudentAge = es.submit(() -> {
                Object studentAge = studentService.getSutdentAge();
                countDownLatch.countDown();
                return studentAge;
            });

            Future futureStudentFamilyInfo = es.submit(() -> {
                Object studentFamilyInfo = studentService.getSutdentFamilyInfo();
                countDownLatch.countDown();
                return studentFamilyInfo;
            });

            //同步等待所有线程执行完之后再继续
            countDownLatch.await();

            resultMap.put("studentName", futureStudentName.get());
            resultMap.put("studentAge", futureStudentAge.get());
            resultMap.put("studentFamilyInfo", futureStudentFamilyInfo.get());
        } catch (Exception e) {
            resultMap.put("errMsg", e.getMessage());
        }

        resultMap.put("total cost", System.currentTimeMillis() - start);

        return resultMap;
    }

    @RequestMapping("/getStudentInfoWithCompletableFuture")
    public Object getStudentInfoWithCompletableFuture() {
        long start = System.currentTimeMillis();
        Map<String, Object> resultMap = new HashMap<>(10);

        try {
            CompletableFuture<Object> completableFutureStudentName = CompletableFuture.supplyAsync(() -> {
                try {
                    return studentService.getStudentName();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            });

            CompletableFuture<Object> completableFutureSutdentAge = CompletableFuture.supplyAsync(() -> {
                try {
                    return studentService.getSutdentAge();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            });

            CompletableFuture<Object> completableFutureFamilyInfo = CompletableFuture.supplyAsync(() -> {
                try {
                    return studentService.getSutdentFamilyInfo();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            });

            CompletableFuture.allOf(completableFutureStudentName, completableFutureSutdentAge, completableFutureFamilyInfo).join();

            resultMap.put("studentName", completableFutureStudentName.get());
            resultMap.put("studentAge", completableFutureSutdentAge.get());
            resultMap.put("studentFamilyInfo", completableFutureFamilyInfo.get());

        } catch (Exception e) {
            resultMap.put("errMsg", e.getMessage());
        }

        resultMap.put("total cost", System.currentTimeMillis() - start);

        return resultMap;
    }


    @RequestMapping("/getStudentInfoWithRxJava")
    public Object getStudentInfoWithRxJava() {
        long start = System.currentTimeMillis();
        Map<String, Object> resultMap = new HashMap<>(10);

        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Observable studentNameObservable = Observable.create(observableEmitter -> {
                resultMap.put("studentName", studentService.getStudentName());
                observableEmitter.onComplete();
            }).subscribeOn(Schedulers.io());

            Observable studentAgeObservable = Observable.create(observableEmitter -> {
                resultMap.put("studentAge", studentService.getSutdentAge());
                observableEmitter.onComplete();
            }).subscribeOn(Schedulers.io());

            Observable familyInfoObservable = Observable.create(observableEmitter -> {
                resultMap.put("studentFamilyInfo", studentService.getSutdentFamilyInfo());
                observableEmitter.onComplete();
            }).subscribeOn(Schedulers.io());
            //创建一个下游 Observer
            Observer<Object> observer = new Observer<Object>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Object o) {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                    //因为后面用了 merge 操作符，所以会合并后发射，那么只要 countdown 一次就行了。
                    countDownLatch.countDown();
                }
            };
            //建立连接,
            Observable.merge(studentNameObservable, studentAgeObservable, familyInfoObservable).subscribe(observer);
            //等待异步线程完成
            countDownLatch.await();

        } catch (Exception e) {
            resultMap.put("errMsg", e.getMessage());
        }
        resultMap.put("total cost", System.currentTimeMillis() - start);
        return resultMap;
    }

}
