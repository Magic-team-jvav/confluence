package org.confluence.terraentity.runtime.dev;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DevOnlyInterceptor implements InvocationHandler  {

    private final Object target;
    static boolean isDevMode = "true".equals(System.getProperty("dev"));

    public DevOnlyInterceptor(Object target) {
        this.target = target;
    }



    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        if(method.getName().equals("toString") || method.getName().equals("hashCode") || method.getName().equals("equals")){
            return method.invoke(target, objects);
        }
        if(isDevMode && method.isAnnotationPresent(DevOnly.class)){
            return null;
        }
        return method.invoke(target, objects);
    }

    public static <T> T createProxy(T target, Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                new Class[]{interfaceClass},
                new DevOnlyInterceptor(target));
    }
}
