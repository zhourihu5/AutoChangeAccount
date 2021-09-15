package com.tencent.autochange.account;

import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SystemServiceIntercept {
    public static void proxyServiceHook(Object object, String filedName, HandlerInvokeCallback callback) {
        // 动态代理
        try {
            Field filed = ReflectUtils.getFiled(object, filedName);
            Object originalObject = filed.get(object);
            Object newProxyObject = newProxyInstance(originalObject, callback);
            filed.set(object, newProxyObject);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object newProxyInstance(Object object, HandlerInvokeCallback callback) {
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                object.getClass().getInterfaces(),
                new ServiceInvocationHandler(object, callback));
    }

    private static final class ServiceInvocationHandler implements InvocationHandler {
        private Object mOriginalObject;
        private HandlerInvokeCallback mCallback;

        public ServiceInvocationHandler(Object object, HandlerInvokeCallback callback) {
            mOriginalObject = object;
            mCallback = callback;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            mCallback.beforeInvoke(method, args);
            Object object =  method.invoke(mOriginalObject, args);
            object = mCallback.afterInvoke(object);
            return object;
        }
    }
}
