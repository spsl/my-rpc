package com.xmutca.rpc.consumer.filter;

import com.xmutca.rpc.core.common.ExtensionGroup;
import com.xmutca.rpc.core.common.ExtensionWrapper;
import com.xmutca.rpc.core.rpc.filter.Filter;
import com.xmutca.rpc.core.rpc.invoke.RpcInvoker;
import com.xmutca.rpc.core.rpc.RpcRequest;
import com.xmutca.rpc.core.rpc.RpcResponse;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-11-01
 */
@ExtensionWrapper(group = ExtensionGroup.ALL, order = 1002)
public class TestFiler2 implements Filter {

    @Override
    public RpcResponse invoke(RpcInvoker<RpcRequest, RpcResponse> rpcInvoker, RpcRequest rpcRequest) {
        System.out.println("TestFiler2 in");
        RpcResponse resp = rpcInvoker.invoke(rpcRequest);
        System.out.println("TestFiler2 out");
        return resp;
    }
}
