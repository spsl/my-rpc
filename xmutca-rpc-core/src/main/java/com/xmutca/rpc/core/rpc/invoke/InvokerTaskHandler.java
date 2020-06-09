package com.xmutca.rpc.core.rpc.invoke;

import com.xmutca.rpc.core.config.RpcServerConfig;
import com.xmutca.rpc.core.rpc.RpcResponse;
import com.xmutca.rpc.core.transport.Transporter;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 服务调用任务处理
 */
public class InvokerTaskHandler extends Thread {

    private ConcurrentLinkedDeque<InvokerTask> queue;

    private ThreadPoolExecutor threadPoolExecutor;

    public InvokerTaskHandler(RpcServerConfig rpcServerConfig) {
        queue = new ConcurrentLinkedDeque<>();
        // 初始化线程池
        threadPoolExecutor = new ThreadPoolExecutor(
                64,
                64,
                60L,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>(1000),
                new DefaultThreadFactory("ServerExecute", true));
    }

    public void addTask(InvokerTask task) {
        queue.add(task);
    }

    @Override
    public void run() {

        while (true) {
            InvokerTask task = queue.poll();
            if (task != null) {
                threadPoolExecutor.submit(() -> {
                    // 执行并返回
                    RpcResponse resp = task.getRpcInvoker().invoke( task.getRequest());
                    // 设置返回体
                    Transporter responseTransporter = task.getMsg().copyAndSet(resp);
                    task.getCtx().writeAndFlush(responseTransporter);
                });
            }

        }

    }

}
