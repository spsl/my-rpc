package com.xmutca.rpc.core.rpc.invoke;

import com.xmutca.rpc.core.common.JavassistHelper;
import com.xmutca.rpc.core.common.SingleClassLoader;
import com.xmutca.rpc.core.exception.RpcException;
import javassist.*;

import java.lang.reflect.Method;
import java.util.UUID;

public class JavassistInvoker<T> implements ServiceInvoker<T> {

    private final Object service;

    private final Method method;

    private final Class<?> clazz;

    private final Class<?>[] parameterTypes;
    private final String[] parameterNames;
    private final int parameterCount;

    private final ServiceInvoker<T> realInvoker;

    public JavassistInvoker(Class<?> clazz, Object service, Method method) {
        this.service = service;
        this.clazz = clazz;
        this.method = method;
        this.parameterTypes = method.getParameterTypes();
        this.parameterCount = parameterTypes.length;
        this.parameterNames = new String[parameterCount];

        try {
            this.realInvoker = generateInvoker();
        } catch (Exception e) {
            throw new RpcException(e);
        }
    }

    private ServiceInvoker<T> generateInvoker() throws Exception{
        final String invokerClassName = "rpc.turbo.invoke.generate.Invoker_"
                + service.getClass().getName()
                + "_" //
                + UUID.randomUUID().toString().replace("-", "");

        // 创建类
        ClassPool pool = ClassPool.getDefault();
        CtClass invokerCtClass = pool.makeClass(invokerClassName);
        invokerCtClass.setInterfaces(new CtClass[] { pool.getCtClass(ServiceInvoker.class.getName()) });

        // 添加私有成员service
        CtField serviceField = new CtField(pool.get(service.getClass().getName()), "service", invokerCtClass);
        serviceField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
        invokerCtClass.addField(serviceField);

        // 添加有参的构造函数
        CtConstructor constructor = new CtConstructor(new CtClass[] { pool.get(service.getClass().getName()) },
                invokerCtClass);
        constructor.setBody("{$0.service = $1;}");
        invokerCtClass.addConstructor(constructor);

        {// 添加通用方法
            StringBuilder methodBuilder = new StringBuilder();
            StringBuilder resultBuilder = new StringBuilder();

            methodBuilder.append("public Object invoke(Object[] params) {\r\n");

            methodBuilder.append("  return ");

            resultBuilder.append("service.");
            resultBuilder.append(method.getName());
            resultBuilder.append("(");

            for (int i = 0; i < parameterCount; i++) {
                Class<?> paramType = parameterTypes[i];

                resultBuilder.append("((");
                resultBuilder.append(JavassistHelper.forceCast(paramType));

                resultBuilder.append(")params[");
                resultBuilder.append(i);
                resultBuilder.append("])");

                resultBuilder.append(JavassistHelper.unbox(paramType));
            }

            resultBuilder.append(")");

            String resultStr = JavassistHelper.box(method.getReturnType(), resultBuilder.toString());

            methodBuilder.append(resultStr);
            methodBuilder.append(";\r\n}");

            CtMethod m = CtNewMethod.make(methodBuilder.toString(), invokerCtClass);
            invokerCtClass.addMethod(m);
        }


        byte[] bytes = invokerCtClass.toBytecode();
        Class<?> invokerClass = SingleClassLoader.loadClass(service.getClass().getClassLoader(), bytes);

        // 通过反射创建有参的实例
        @SuppressWarnings("unchecked")
        ServiceInvoker<T> invoker = (ServiceInvoker<T>) invokerClass.getConstructor(service.getClass()).newInstance(service);

        return invoker;
    }


    @Override
    public T invoke() {
        return realInvoker.invoke();
    }

    @Override
    public T invoke(Object... params) {
        if (parameterCount < 1) {
            return invoke();
        }
        return realInvoker.invoke(params);
    }



}
