package com.cww.ribbon.consumer;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.ObservableExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.ExecutionException;

/**
 * Created by JackWangon[www.coder520.com] 2017/11/28.
 */
@Service
public class HelloService {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 简单版本
     * @return
     */
    @HystrixCommand(fallbackMethod = "helloFallBack")
    public  String helloService1() {
        return restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
    }

    /**
     * 1、 使用hystrix进行服务降级（注解方式和非注解方式），就是当我请求一个服务超过2s时 断开请求
     * 返回 helloFallBack 中的错误
     * 2、 对于每个经过 此服务的请求
     * 正常情况下是io（阻塞的）nio是非阻塞 callable 和future
     * (如果是io的 调用三个服务 就是 t1,t2 ,t3 如果nio 就是三者中的最大值)
     * 3、此处使用观察者模式进行（来获取每个服务返回的信息）
     * 4、观察者模式的冷热执行 LAZY或者EAGER
     * 冷执行 等待其事件出事化完成在执行请求 热 反之
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @HystrixCommand(fallbackMethod = "helloFallBack", observableExecutionMode = ObservableExecutionMode.EAGER)
    public Observable<String> helloService2() throws ExecutionException, InterruptedException {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    //要订阅的才执行该逻辑
                    if (!subscriber.isUnsubscribed()) {
                        System.out.println("方法执行。。。");
                        String result = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
//                        传递结果
                        subscriber.onNext(result);
//                        String result1 = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class).getBody();
//                        subscriber.onNext(result1);
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }

            }
        });

    }



    public String helloFallBack(Throwable throwable) {
        return "服务挂了啊";
    }




}
