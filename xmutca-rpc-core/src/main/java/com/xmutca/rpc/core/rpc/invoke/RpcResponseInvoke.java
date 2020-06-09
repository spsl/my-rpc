package com.xmutca.rpc.core.rpc.invoke;

import com.alibaba.fastjson.JSON;
import com.xmutca.rpc.core.common.InvokerUtils;
import com.xmutca.rpc.core.exception.RpcException;
import com.xmutca.rpc.core.rpc.RpcRequest;
import com.xmutca.rpc.core.rpc.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-01-02
 */
@Slf4j
public class RpcResponseInvoke implements RpcInvoker<RpcRequest, RpcResponse> {

    private static final Map<String, Object> EXPORT_PROVIDER = new ConcurrentHashMap<>();


    private static final Map<String, JavassistInvoker> javassistInvokerMap = new ConcurrentHashMap<>();

    /**
     * 导出提供者
     * @param interFaceName
     * @param object
     */
    public static void addProvider(String interFaceName, Object object) {
        // 对service进行代理生成

        Method[] methods = object.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (Modifier.isPublic(method.getModifiers())
                    && !Modifier.isStatic(method.getModifiers())) {

                String methodName = method.getName();
                Class<?>[] paramTypeClasses = method.getParameterTypes();
                String key = getKey(interFaceName, methodName, InvokerUtils.calculateMethodSign(interFaceName, methodName, paramTypeClasses));
                JavassistInvoker realInvoker = new JavassistInvoker(object.getClass(), object, method);
                javassistInvokerMap.put(key, realInvoker);
            }
        }

        EXPORT_PROVIDER.putIfAbsent(interFaceName, object);
    }

    /**
     * 执行具体方法
     * @param rpcRequest
     * @return
     */
    @Override
    public RpcResponse invoke(RpcRequest rpcRequest) {
        log.info("receive message: {}", JSON.toJSONString(rpcRequest));

        RpcResponse rpcResponse = new RpcResponse();
        Object serviceBean = EXPORT_PROVIDER.get(rpcRequest.getClassName());
        if (null == serviceBean) {
            rpcResponse.setException(new RpcException("provider not found"));
            return rpcResponse;
        }

        String methodName = rpcRequest.getMethodName();
        Object[] parameters = rpcRequest.getArguments();

        String key = getKey(rpcRequest.getClassName(), methodName, rpcRequest.getMethodSign());

        JavassistInvoker realInvoker = javassistInvokerMap.get(key);
        if (Objects.isNull(realInvoker)) {
            rpcResponse.setException(new RpcException("provider not found"));
            return rpcResponse;
        }
        try {
            Object result = realInvoker.invoke(parameters);
            rpcResponse.setResult(result);
            return rpcResponse;
        } catch (Exception e) {
            rpcResponse.setException(new RpcException("provider execute ex", e));
            return rpcResponse;
        }
    }

    private static String getKey(String className, String methodName, String methodSign) {
        return className + "#" + methodName + "_" + methodSign;
    }

}
