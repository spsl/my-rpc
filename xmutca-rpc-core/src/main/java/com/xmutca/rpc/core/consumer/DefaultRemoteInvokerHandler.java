package com.xmutca.rpc.core.consumer;

import com.xmutca.rpc.core.common.ExtensionLoader;
import com.xmutca.rpc.core.config.RpcClientConfig;
import com.xmutca.rpc.core.config.RpcMetadata;
import com.xmutca.rpc.core.exception.RpcException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRemoteInvokerHandler implements RemoteInvokerHandler {

    private final Map<String, GenericInvoker> invokerMap;


    public DefaultRemoteInvokerHandler(List<RpcClientConfig> clientConfigs) {
        invokerMap = new HashMap<>();
        if (null != clientConfigs) {
            clientConfigs.forEach(config -> {
                LoadBalancer loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).newInstanceForMethod(config.getLoadBalancer(), "getInstance");
                ClusterInvoker cluster = ExtensionLoader.getExtensionLoader(ClusterInvoker.class).newInstance(config.getCluster());
                cluster.init(config.getMetadata(), loadBalancer, config);
                invokerMap.put(generateKey(config.getMetadata()), cluster);
            });
        }
    }

    @Override
    public Object invoke(RpcMetadata metadata, String serviceName, String methodName, String methodSign, Object[] args) {
        GenericInvoker strategyInvoker = invokerMap.get(generateKey(metadata));
        if (null == strategyInvoker) {
            throw new RpcException("没有对应的invoker");
        }
        return strategyInvoker.invoke(serviceName, methodName, methodSign, args);
    }

    private String generateKey(RpcMetadata metadata) {
        return metadata.getGroup() + "." + metadata.getServiceName();
    }
}
