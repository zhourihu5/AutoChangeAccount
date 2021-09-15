package com.tencent.autochange.account;

import java.lang.reflect.Field;

public class ReflectUtils {

    public static Object getFiledObject(Object object, String filedName) {
        try {
            Field field = object.getClass().getDeclaredField(filedName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getFiled(Object object, String filedName) {
        try {
            Field field = object.getClass().getDeclaredField(filedName);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
