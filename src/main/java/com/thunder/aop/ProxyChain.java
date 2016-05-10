package com.thunder.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by icepoint1999 on 3/24/16.
 */

//代理链

public class ProxyChain {

//        目标类
    private final Class<?> targetClass;

//    目标对象
    private final Object targetObject;

//    目标方法
    private final Method targerMethod;

//    代理方法
    private final MethodProxy methodProxy;
    private final Object[] params;

    private final Class<? extends  Annotation> annotation;


    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }



    private List<Proxy> proxyList = new ArrayList<Proxy>();
    private  int  proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targerMethod, MethodProxy methodProxy, Object[] params, List<Proxy> proxyList,Class<? extends Annotation> annotation) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targerMethod = targerMethod;
        this.methodProxy = methodProxy;
        this.params = params;
        this.proxyList = proxyList;
        this.annotation = annotation;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getTargerMethod() {
        return targerMethod;
    }

    public MethodProxy getMethodProxy() {
        return methodProxy;
    }

    public Object[] getParams() {
        return params;
    }

    public List<Proxy> getProxyList() {
        return proxyList;
    }

    public int getProxyIndex() {
        return proxyIndex;
    }

    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex<proxyList.size()){
            methodResult = proxyList.get(proxyIndex++).doProxy(this);
        }else{
            methodResult = methodProxy.invokeSuper(targetObject,params);
        }
        return methodResult;
    }
}
