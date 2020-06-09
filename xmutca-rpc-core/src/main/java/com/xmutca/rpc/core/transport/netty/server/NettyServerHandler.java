package com.xmutca.rpc.core.transport.netty.server;

import com.xmutca.rpc.core.common.TransportType;
import com.xmutca.rpc.core.rpc.invoke.RpcInvoker;
import com.xmutca.rpc.core.rpc.RpcRequest;
import com.xmutca.rpc.core.rpc.RpcResponse;
import com.xmutca.rpc.core.rpc.invoke.InvokerTask;
import com.xmutca.rpc.core.rpc.invoke.InvokerTaskHandler;
import com.xmutca.rpc.core.transport.Transporter;
import com.xmutca.rpc.core.transport.netty.AbstractHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-01-02
 */
@Slf4j
public class NettyServerHandler extends AbstractHandler {

    private RpcInvoker<RpcRequest, RpcResponse> rpcInvoker;

    private InvokerTaskHandler invokerTaskHandler;

    public NettyServerHandler(RpcInvoker<RpcRequest, RpcResponse> rpcInvoker, InvokerTaskHandler invokerTaskHandler) {
        this.rpcInvoker = rpcInvoker;
        this.invokerTaskHandler = invokerTaskHandler;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Transporter msg) {
        if (TransportType.get(msg.getType()) == TransportType.MODE_HEARTBEAT) {
            log.info("receive heartbeat message");
            return;
        }

        invokerTaskHandler.addTask(new InvokerTask(rpcInvoker, ctx, msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("netty server caught exception", cause);
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 不需要往下执行
        if (!checkInstanceofIdleStateEvent(evt)) {
            super.userEventTriggered(ctx, evt);
            return;
        }

        // 读超时关闭通道
        IdleState state = ((IdleStateEvent) evt).state();
        if (state == IdleState.READER_IDLE) {
            ctx.channel().close();      // beat 3N, close if idle
        }
    }
}
