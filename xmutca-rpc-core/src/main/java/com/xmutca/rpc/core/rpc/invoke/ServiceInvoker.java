package com.xmutca.rpc.core.rpc.invoke;

public interface ServiceInvoker<T> {

    T invoke();

    T invoke(Object[] params);
}
