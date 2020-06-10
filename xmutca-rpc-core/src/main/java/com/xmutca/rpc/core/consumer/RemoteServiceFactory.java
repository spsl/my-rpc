package com.xmutca.rpc.core.consumer;

import com.xmutca.rpc.core.common.InvokerUtils;
import com.xmutca.rpc.core.config.RpcMetadata;
import com.xmutca.rpc.core.exception.RpcException;
import javassist.*;

import java.lang.reflect.Method;
import java.util.UUID;

public class RemoteServiceFactory {

    private RemoteServiceFactory(RpcMetadata metadata, RemoteInvokerHandler handler) {
        this.metadata = metadata;
        this.handler = handler;
    }

    /**
     * 元数据
     */
    private final RpcMetadata metadata;

    private final RemoteInvokerHandler handler;

    public static RemoteServiceFactory factory(RpcMetadata metadata, RemoteInvokerHandler handler) {
        return new RemoteServiceFactory(metadata, handler);
    }

    public <T> T getService(Class<?> remoteService) {
        try {
            return (T) generateInterfaceImplements(remoteService, handler, metadata);
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    private static <T> T generateInterfaceImplements(Class<T> clazz, RemoteInvokerHandler handler, RpcMetadata metadata) {
        // 创建类
        final String proxyClassName = "rpc.turbo.invoke.generate.Proxy_"
                + clazz.getName()
                + "_"
                + UUID.randomUUID().toString().replace("-", "");

        T proxyObject = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass proxyClass = pool.makeClass(proxyClassName); //创建代理类对象

            // 添加私有成员invokerHandler
            CtField invokerField = new CtField(pool.get(RemoteInvokerHandler.class.getName()), "handler", proxyClass);
            invokerField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
            proxyClass.addField(invokerField);
            // 添加私有成员metaData
            CtField metadataField = new CtField(pool.get(RpcMetadata.class.getName()), "metadata", proxyClass);
            metadataField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
            proxyClass.addField(metadataField);

            // 添加有参的构造函数
            CtConstructor constructor = new CtConstructor(new CtClass[] { pool.get(RemoteInvokerHandler.class.getName()), pool.get(RpcMetadata.class.getName()) },
                    proxyClass);
            constructor.setBody("{$0.handler = $1; $0.metadata = $2; }");
            proxyClass.addConstructor(constructor);

            proxyClass.setInterfaces(new CtClass[]{pool.getCtClass(clazz.getName())});
            Method[] methods = clazz.getDeclaredMethods();
            for(Method method:methods){
                String methodName = method.getName();

                StringBuilder methodBuilder = new StringBuilder();

                methodBuilder.append("public ");
                methodBuilder.append(method.getReturnType().getName());
                methodBuilder.append(" ");
                methodBuilder.append(method.getName());
                methodBuilder.append("(");
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    Class<?> parameterType = method.getParameterTypes()[i];

                    methodBuilder.append(parameterType.getName());
                    methodBuilder.append(" param");
                    methodBuilder.append(i);
                    if (i != method.getParameterTypes().length - 1) {
                        methodBuilder.append(", ");
                    }
                }

                methodBuilder.append("){\r\n");
                if (method.getParameterTypes().length == 0) {
                    methodBuilder.append("Object[] params = new Object[0];");
                } else {
                    methodBuilder.append("Object[] params = new Object[]{");
                    for (int i = 0; i < method.getParameterTypes().length; i++) {
                        methodBuilder.append("param");
                        methodBuilder.append(i);
                        if (i != method.getParameterTypes().length - 1) {
                            methodBuilder.append(", ");
                        }
                    }
                    methodBuilder.append("};\r\n ");
                }


                methodBuilder.append(" java.lang.String serviceName = \"");
                methodBuilder.append(clazz.getName());
                methodBuilder.append("\";\r\n ");


                methodBuilder.append(" String methodName = \"");
                methodBuilder.append(method.getName());
                methodBuilder.append("\";\r\n ");



                methodBuilder.append(" String methodSign = \"");
                methodBuilder.append(InvokerUtils.calculateMethodSign(clazz.getName(), methodName, method.getParameterTypes()));
                methodBuilder.append("\";\r\n ");


                methodBuilder.append(" return (");
                methodBuilder.append(method.getReturnType().getName());
                methodBuilder.append(") handler.invoke(metadata, serviceName, methodName, methodSign, params); \r\n }");

                CtMethod m = CtNewMethod.make(methodBuilder.toString(), proxyClass);
                proxyClass.addMethod(m);
            }

            Class<?> invokerClass = proxyClass.toClass();

            return(T) invokerClass.getConstructor(RemoteInvokerHandler.class, RpcMetadata.class).newInstance(handler, metadata);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxyObject;
    }


//    public GenericInvoker newProxyInstance() {
//        // check metadata
//        if (Objects.isNull(metadata)) {
//            throw new RuntimeException("元数据配置不能为空");
//        }
//
//        RpcClientConfig rpcClientConfig = ConsumerConfigHolder.get(metadata.getUniqueMetaName());
//
//        // check rpcClientConfig
//        if (Objects.isNull(rpcClientConfig)) {
//            throw new RuntimeException("客户端配置不能为空");
//        }
//
//        // new and init
//        LoadBalancer loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).newInstanceForMethod(rpcClientConfig.getLoadBalancer(), "getInstance");
//        ClusterInvoker cluster = ExtensionLoader.getExtensionLoader(ClusterInvoker.class).newInstance(rpcClientConfig.getCluster());
//        cluster.init(rpcClientConfig.getMetadata(), loadBalancer, rpcClientConfig, target.getName());
//        return cluster;
//    }

    public java.lang.Object test(){
        Object[] params = new Object[]{};
        String serviceName = "com.xmutca.rpc.consumer.ControllerTest";
        String methodName = "test";
        String methodSign = "394847028a515dbe7ce8ef5c719ed327";
        return (java.lang.Object) handler.invoke(metadata, serviceName, methodName, methodSign, params);
    }
}
