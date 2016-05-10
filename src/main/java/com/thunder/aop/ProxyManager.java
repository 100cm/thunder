package com.thunder.aop;

import com.thunder.helper.BeanHelper;
import com.thunder.util.MethodUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.ehcache.hibernate.management.impl.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by icepoint1999 on 3/24/16.
 */
public class ProxyManager {


    public  static <T> T createProxy(final AopTarget aopTarget , final List<Proxy> proxyList){

        return (T) Enhancer.create(aopTarget.getCls(), new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {


                    return new ProxyChain(aopTarget.getCls(),o,method,methodProxy,objects,proxyList,aopTarget.getAnnotation()).doProxyChain();


            }
        });

    }
}
