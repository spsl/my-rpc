package com.xmutca.rpc.core.consumer;

import com.xmutca.rpc.core.config.RpcMetadata;

public interface RemoteInvokerHandler {

    Object invoke(RpcMetadata metadata, String serviceName, String methodName, String methodSign, Object[] args);
}
