package com.thunder.aop;

import com.thunder.Annotation.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by icepoint1999 on 3/24/16.
 */
public class AspectProxy  implements  Proxy{
    @Override
    public Object doProxy(ProxyChain proxyChain) throws Throwable {
        Object result  = null;
        Class<?> c =  proxyChain.getTargetClass();
        Class<? extends Annotation> a = proxyChain.getAnnotation();
        Method  method = proxyChain.getTargerMethod();
        if(method.getAnnotationsByType(a).length==0){
            result = proxyChain.doProxyChain();
            return result;
        }
        Object[] params = proxyChain.getParams();
        begin();
        try {
            if (intercept(c,method,params)){
                before(c,method,params);
                result = proxyChain.doProxyChain();
                after(c,method,params,result);
            }else{
                result = proxyChain.doProxyChain();
            }
        }catch (Exception e){
            error(c,method,params,e);
            throw  e;
        }finally {
          end();
        }

        return result;
    }
    public  void  begin(){

    }

    public boolean intercept(Class<?> c ,Method method,Object[] params)throws Throwable{

        return true;
    }
    public void before(Class<?> c ,Method method,Object[] params)throws Throwable{


    }
    public void after(Class<?> c ,Method method,Object[] params , Object o)throws Throwable{


    }
    public void error(Class<?> c ,Method method,Object[] params,Exception e)throws Throwable{


    }
    public void end(){

    }
}
