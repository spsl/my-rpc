package com.xmutca.rpc.core.rpc.invoke;

/**
 * invoke -> directory -> route -> invoke
 * @author qudian
 */
public interface RpcInvoker<T, R> {

    /**
     * 远程执行
     * @param req
     * @return
     */
    R invoke(T req);
}
