package com.xmutca.rpc.consumer;

import com.xmutca.rpc.core.consumer.Reference;
import com.xmutca.rpc.example.api.HelloService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2020/5/26
 */
@RestController
@RequestMapping
public class ControllerTest {

    @Reference(serviceName = "test", group = "order", interfaceClass = HelloService.class)
    private HelloService helloService;

    @RequestMapping("/hello")
    public Object test() {
        return helloService.sayHello();
    }

    @RequestMapping("/echo")
    public Object echo(String text) {
        return helloService.echo(text);
    }

    public static void main(String[] args) {
        ttt();
    }

    public static java.lang.Object ttt(){
        Object[] params = new Object[0];
        String serviceName = "com.xmutca.rpc.consumer.ControllerTest";
        String methodName = "test";
        String methodSign = "394847028a515dbe7ce8ef5c719ed327";
        return null;
    }
}
