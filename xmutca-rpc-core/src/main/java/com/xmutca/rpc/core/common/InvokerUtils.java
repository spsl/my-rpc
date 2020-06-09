package com.xmutca.rpc.core.common;

import sun.security.provider.MD5;

import java.util.Arrays;

public class InvokerUtils {

    public static String calculateMethodSign(String className, String methodName, Class<?>[] typeClasses) {
        String finalStr = className + "#" + methodName + Arrays.toString(typeClasses);
        return MD5Utils.md5(finalStr);
    }
}
