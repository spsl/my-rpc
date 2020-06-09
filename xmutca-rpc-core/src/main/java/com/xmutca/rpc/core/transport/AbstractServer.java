package com.xmutca.rpc.core.transport;

import com.xmutca.rpc.core.common.ExtensionGroup;
import com.xmutca.rpc.core.config.RpcServerConfig;
import com.xmutca.rpc.core.rpc.RpcRequest;
import com.xmutca.rpc.core.rpc.RpcResponse;
import com.xmutca.rpc.core.rpc.filter.FilterWrapper;
import com.xmutca.rpc.core.rpc.invoke.RpcInvoker;
import com.xmutca.rpc.core.rpc.invoke.InvokerTaskHandler;
import com.xmutca.rpc.core.rpc.invoke.RpcResponseInvoke;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-11-03
 */
public abstract class AbstractServer implements Server {

    private RpcServerConfig rpcServerConfig;

    private InvokerTaskHandler invokerTaskHandler;
    private RpcInvoker<RpcRequest, RpcResponse> rpcInvoker = FilterWrapper.buildInvokeChain(new RpcResponseInvoke(), ExtensionGroup.PROVIDER);

    @Override
    public void init(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;

        // 初始化
        doOpen();

        invokerTaskHandler = new InvokerTaskHandler(rpcServerConfig);
        invokerTaskHandler.start();
    }

    @Override
    public void publish(String interfaceName, Object object) {
        if (null == object) {
            return;
        }

        // 加入提供者列表
        RpcResponseInvoke.addProvider(interfaceName, object);
    }

    /**
     * 创建连接
     */
    protected abstract void doOpen();

    /**
     * 获取执行器
     * @return
     */
    public RpcInvoker<RpcRequest, RpcResponse> getRpcInvoker() {
        return rpcInvoker;
    }

    protected InvokerTaskHandler getInvokerTaskHandler() {
        return invokerTaskHandler;
    }


    /**
     * 获取端口
     * @return
     */
    protected int getPort() {
        return rpcServerConfig.getPort();
    }
}
