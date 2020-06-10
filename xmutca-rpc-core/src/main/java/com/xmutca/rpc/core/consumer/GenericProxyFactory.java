package com.xmutca.rpc.core.consumer;

import com.xmutca.rpc.core.common.ExtensionLoader;
import com.xmutca.rpc.core.common.InvokerUtils;
import com.xmutca.rpc.core.common.SingleClassLoader;
import com.xmutca.rpc.core.config.RpcClientConfig;
import com.xmutca.rpc.core.config.RpcMetadata;
import com.xmutca.rpc.core.rpc.invoke.ServiceInvoker;
import javassist.*;
import org.assertj.core.util.Preconditions;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;
import java.util.UUID;

/**
 * @version Revision: 0.0.1
 * @author: weihuang.peng
 * @Date: 2019-11-10
 */
public class GenericProxyFactory {

    private GenericProxyFactory() {

    }

    /**
     * 元数据
     */
    private RpcMetadata metadata;

    /**
     * 类名
     */
    private Class<?> target;

    /**
     * get factory
     *
     * @param target
     * @return
     */
    public static GenericProxyFactory factory(Class<?> target) {
        GenericProxyFactory factory = new GenericProxyFactory();
        factory.target = target;
        return factory;
    }

    /**
     * set metadata
     *
     * @param metadata
     * @return
     */
    public GenericProxyFactory metadata(RpcMetadata metadata) {
        this.metadata = metadata;
        return this;
    }

    /**
     * 获取具体实例
     *
     * @param <T>
     * @return
     */
//    public <T> T getReferenceBean() {
//        GenericInvoker invoker = this.newProxyInstance();
//        return (T) Proxy.newProxyInstance(
//                Thread.currentThread().getContextClassLoader(),
//                new Class[]{target},
//                (Object proxy, Method method, Object[] args) -> invoker.invoke(method.getName(), method.getParameterTypes(), args));
//    }

    public <T> T getReferenceBean() {
        GenericInvoker invoker = this.newProxyInstance();
        try {
            return (T) generateInterfaceImplements(target, invoker);
        } catch (NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 生成代理类
     * @param <T>
     * @return
     */
    public <T> T generateReferenceBean(int a, int b, Integer c) {

        return null;
    }

    private static <T> T generateInterfaceImplements(Class<T> clazz, GenericInvoker invoker) throws NotFoundException {
        // 创建类
        final String proxyClassName = "rpc.turbo.invoke.generate.Proxy_"
                + clazz.getName()
                + "_"
                + UUID.randomUUID().toString().replace("-", "");

        T proxyObject = null;
        try {
            ClassPool pool = ClassPool.getDefault();
            CtClass proxyClass = pool.makeClass(proxyClassName); //创建代理类对象

            // 添加私有成员service
            CtField invokerField = new CtField(pool.get(GenericInvoker.class.getName()), "invoker", proxyClass);
            invokerField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
            proxyClass.addField(invokerField);

            // 添加有参的构造函数
            CtConstructor constructor = new CtConstructor(new CtClass[] { pool.get(GenericInvoker.class.getName()) },
                    proxyClass);
            constructor.setBody("{$0.invoker = $1;}");
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
                methodBuilder.append("Object[] params = new Object[]{");
                for (int i = 0; i < method.getParameterTypes().length; i++) {
                    methodBuilder.append("param");
                    methodBuilder.append(i);
                    if (i != method.getParameterTypes().length - 1) {
                        methodBuilder.append(", ");
                    }
                }
                methodBuilder.append("};\r\n");

                methodBuilder.append("String serviceName = \"");
                methodBuilder.append(clazz.getName());
                methodBuilder.append("\";\r\n");


                methodBuilder.append("String methodName = \"");
                methodBuilder.append(method.getName());
                methodBuilder.append("\";\r\n");



                methodBuilder.append("String methodSign = \"");
                methodBuilder.append(InvokerUtils.calculateMethodSign(clazz.getName(), methodName, method.getParameterTypes()));
                methodBuilder.append("\";\r\n");


                methodBuilder.append(" return (");
                methodBuilder.append(method.getReturnType().getName());
                methodBuilder.append(") invoker.invoke(serviceName, methodName, methodSign, params); \r\n }");


                CtMethod m = CtNewMethod.make(methodBuilder.toString(), proxyClass);
                proxyClass.addMethod(m);
            }

            Class<?> invokerClass = proxyClass.toClass();

            return(T) invokerClass.getConstructor(GenericInvoker.class).newInstance(invoker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proxyObject;
    }



    /**
     * 创建新代理
     *
     * @return
     */
    public GenericInvoker newProxyInstance() {
        // check metadata
        if (Objects.isNull(metadata)) {
            throw new RuntimeException("元数据配置不能为空");
        }

        RpcClientConfig rpcClientConfig = ConsumerConfigHolder.get(metadata.getUniqueMetaName());

        // check rpcClientConfig
        if (Objects.isNull(rpcClientConfig)) {
            throw new RuntimeException("客户端配置不能为空");
        }

        // new and init
        LoadBalancer loadBalancer = ExtensionLoader.getExtensionLoader(LoadBalancer.class).newInstanceForMethod(rpcClientConfig.getLoadBalancer(), "getInstance");
        ClusterInvoker cluster = ExtensionLoader.getExtensionLoader(ClusterInvoker.class).newInstance(rpcClientConfig.getCluster());
        cluster.init(rpcClientConfig.getMetadata(), loadBalancer, rpcClientConfig, target.getName());
        return cluster;
    }
}
