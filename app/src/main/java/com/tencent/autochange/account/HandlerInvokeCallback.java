package com.tencent.autochange.account;

import java.lang.reflect.Method;

public interface HandlerInvokeCallback {
    void beforeInvoke(Method method, Object[] args);
    Object afterInvoke(Object object);
}
