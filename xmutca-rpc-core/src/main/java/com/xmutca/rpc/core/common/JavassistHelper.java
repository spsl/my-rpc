package com.xmutca.rpc.core.common;

public class JavassistHelper {

    public static String unbox(Class<?> clazz) {
        if (int.class.equals(clazz)) {
            return ".intValue()";
        }

        if (long.class.equals(clazz)) {
            return ".longValue()";
        }

        if (boolean.class.equals(clazz)) {
            return ".booleanValue()";
        }

        if (double.class.equals(clazz)) {
            return ".doubleValue()";
        }

        if (float.class.equals(clazz)) {
            return ".floatValue()";
        }

        if (short.class.equals(clazz)) {
            return ".shortValue()";
        }

        if (byte.class.equals(clazz)) {
            return ".byteValue()";
        }

        if (char.class.equals(clazz)) {
            return ".charValue()";
        }

        return "";
    }



    public static String box(Class<?> clazz, String value) {
        if (int.class.equals(clazz)) {
            return Integer.class.getName() + ".valueOf(" + value + ")";
        }

        if (long.class.equals(clazz)) {
            return Long.class.getName() + ".valueOf(" + value + ")";
        }

        if (boolean.class.equals(clazz)) {
            return Boolean.class.getName() + ".valueOf(" + value + ")";
        }

        if (double.class.equals(clazz)) {
            return Double.class.getName() + ".valueOf(" + value + ")";
        }

        if (float.class.equals(clazz)) {
            return Float.class.getName() + ".valueOf(" + value + ")";
        }

        if (short.class.equals(clazz)) {
            return Short.class.getName() + ".valueOf(" + value + ")";
        }

        if (byte.class.equals(clazz)) {
            return Byte.class.getName() + ".valueOf(" + value + ")";
        }

        if (char.class.equals(clazz)) {
            return Character.class.getName() + ".valueOf(" + value + ")";
        }

        return value;
    }


    public static String forceCast(Class<?> clazz) {
        if (int.class.equals(clazz)) {
            return Integer.class.getName();
        }

        if (long.class.equals(clazz)) {
            return Long.class.getName();
        }

        if (boolean.class.equals(clazz)) {
            return Boolean.class.getName();
        }

        if (double.class.equals(clazz)) {
            return Double.class.getName();
        }

        if (float.class.equals(clazz)) {
            return Float.class.getName();
        }

        if (short.class.equals(clazz)) {
            return Short.class.getName();
        }

        if (byte.class.equals(clazz)) {
            return Byte.class.getName();
        }

        if (char.class.equals(clazz)) {
            return Character.class.getName();
        }

        return clazz.getName();
    }




}
