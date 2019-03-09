package com.cww.ribbon.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class ConsumerController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HelloService helloService ;


    /**
     *
     * @return
     */
    @RequestMapping("helloConsumer")
    public String helloConsumer1() throws ExecutionException, InterruptedException {
        return helloService.helloService1();
    }

    @RequestMapping("helloConsumer2")
    public String helloConsumer2() throws ExecutionException, InterruptedException {
        List<String> list = new ArrayList<>();
        Observable<String> observable = helloService.helloService2();
        observable.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                System.out.println("聚合完了所有的查询请求!");
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(String o) {
                System.out.println("结果来了....");
                list.add(o);
            }
        });
        return list.toString();

    }
}
