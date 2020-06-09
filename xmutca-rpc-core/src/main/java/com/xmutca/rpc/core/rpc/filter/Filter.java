package com.xmutca.rpc.core.rpc.filter;

import com.xmutca.rpc.core.rpc.RpcRequest;
import com.xmutca.rpc.core.rpc.RpcResponse;
import com.xmutca.rpc.core.rpc.invoke.RpcInvoker;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-10-31
 */
public interface Filter {

    /**
     * 远程执行
     * @param rpcInvoker
     * @param req
     * @return
     */
    RpcResponse invoke(RpcInvoker<RpcRequest, RpcResponse> rpcInvoker, RpcRequest req);
}
