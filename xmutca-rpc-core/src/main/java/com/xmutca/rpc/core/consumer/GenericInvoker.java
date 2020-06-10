package com.xmutca.rpc.core.consumer;

import com.xmutca.rpc.core.rpc.RpcRequest;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-11-08
 */
public interface GenericInvoker {

    Object invoke(String serviceName, String methodName, String methodSign, Object[] args);

    /**
     * 泛化调用
     * @param rpcRequest
     * @return
     */
    Object invoke(RpcRequest rpcRequest);
}
