package com.xmutca.rpc.core.rpc.invoke;

import com.xmutca.rpc.core.rpc.RpcRequest;
import com.xmutca.rpc.core.rpc.RpcResponse;
import com.xmutca.rpc.core.transport.Transporter;
import io.netty.channel.ChannelHandlerContext;

public class InvokerTask {

    private RpcInvoker<RpcRequest, RpcResponse> rpcInvoker;

    private RpcRequest request;

    private Transporter msg;

    private ChannelHandlerContext ctx;

    // 进入队列的时间
    private long inQueueTimestamp;

    public InvokerTask(RpcInvoker<RpcRequest, RpcResponse> rpcInvoker, ChannelHandlerContext ctx, Transporter msg) {
        this.rpcInvoker = rpcInvoker;
        this.ctx = ctx;
        this.msg = msg;
        this.request = (RpcRequest) msg.getBody();
        inQueueTimestamp = System.currentTimeMillis();
    }

    public RpcInvoker<RpcRequest, RpcResponse> getRpcInvoker() {
        return rpcInvoker;
    }

    public RpcRequest getRequest() {
        return request;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public Transporter getMsg() {
        return msg;
    }

}
