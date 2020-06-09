package com.xmutca.rpc.core.rpc.filter;

import com.xmutca.rpc.core.common.ExtensionGroup;
import com.xmutca.rpc.core.common.ExtensionLoader;
import com.xmutca.rpc.core.rpc.*;
import com.xmutca.rpc.core.rpc.invoke.RpcInvoker;
import com.xmutca.rpc.core.rpc.invoke.RpcResponseInvoke;

import java.util.Collection;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-10-31
 */
public class FilterWrapper {

    /**
     * 执行链，最后一环是真正的执行方法，但是任意一环都可以提前退出
     * @param rpcInvoker
     * @param group
     * @return
     */
    public static RpcInvoker<RpcRequest, RpcResponse> buildInvokeChain(RpcInvoker<RpcRequest, RpcResponse> rpcInvoker, ExtensionGroup group) {
        RpcInvoker<RpcRequest, RpcResponse> last = rpcInvoker;

        // 初始化chain的过程
        Collection<Filter> invokers = ExtensionLoader.getExtensionLoader(Filter.class).getAllExtensionOfSorted(group);
        for (Filter filter:invokers) {
            RpcInvoker<RpcRequest, RpcResponse> next = last;
            last = rpcRequest -> filter.invoke(next, rpcRequest);
        }
        return last;
    }

    public static void main(String[] args) {
        RpcResponseInvoke rpcResponseInvoke = new RpcResponseInvoke();
        RpcInvoker<RpcRequest, RpcResponse> rpcInvoker = FilterWrapper.buildInvokeChain(rpcResponseInvoke, ExtensionGroup.CONSUMER);
        RpcResponse resp = rpcInvoker.invoke(new RpcRequest());
    }
}
