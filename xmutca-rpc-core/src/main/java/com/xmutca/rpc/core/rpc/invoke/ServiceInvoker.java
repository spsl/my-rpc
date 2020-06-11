package com.xmutca.rpc.core.rpc.invoke;

public interface ServiceInvoker<T> {

    T invoke(Object[] params);
}
