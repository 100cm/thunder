package com.thunder.helper;

import com.thunder.Annotation.Aspect;
import com.thunder.aop.AopTarget;
import com.thunder.aop.AspectProxy;
import com.thunder.aop.Proxy;
import com.thunder.aop.ProxyManager;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Created by icepoint1999 on 3/24/16.
 */
public final class Aophelper {


    static{

        try {
            Map<Class<?>,Set<AopTarget>> proxMap =  createProxyMap();
            Map<AopTarget,List<Proxy>> targetMap = createTargetMap(proxMap);
            for (Map.Entry<AopTarget,List<Proxy>> targetEntry : targetMap.entrySet()){
                AopTarget aopTarget = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                Object proxy = ProxyManager.createProxy(aopTarget,proxyList);
                System.out.println("aop add class" + aopTarget.getCls());
                BeanHelper.setBean(aopTarget.getCls(),proxy);
            }
        } catch (IllegalAccessException e) {
            System.out.print("加载aop失败");
        } catch (InstantiationException e) {
            System.out.print("加载aop失败");
        }

    }

    /**
     * 代理类 目标类 映射关系
     * @return
     */
    private static Map<Class<?>,Set<AopTarget>> createProxyMap(){
        Map<Class<?>,Set<AopTarget>> proxyMap = new HashMap<Class<?>,Set<AopTarget>>();
        Set<Class<?>> proxyClassSet = ClassHelper.getBeanClassSetBySuper(AspectProxy.class);
        for (Class<?> proxyClass : proxyClassSet){
            if (proxyClass.isAnnotationPresent(Aspect.class)){
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<AopTarget> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass,targetClassSet);
            }

        }
        return proxyMap;
    }

    /**
     * 获取aspect注解中设置的注解类.并且把这些类放入集合中,最终返回这个集合
     * @param aspect
     * @return
     */
    private static Set<AopTarget> createTargetClassSet(Aspect aspect){

        Set<AopTarget> targetClassSet = new HashSet<AopTarget>();
        Class<? extends Annotation> annation = aspect.value();
        if (null != annation && !annation.equals(Aspect.class)){
            targetClassSet.addAll(ClassHelper.getAspectClassSetByAnnotation(annation));
        }
        return targetClassSet;
    }

    /**
     * 目标类与代理对象列表之间的映射关系
     * @param proxMap
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static Map<AopTarget,List<Proxy>> createTargetMap(Map<Class<?>,Set<AopTarget>> proxMap) throws IllegalAccessException, InstantiationException {

        Map<AopTarget,List<Proxy>> targetMap =  new HashMap<AopTarget,List<Proxy>>();
        for (Map.Entry<Class<?>,Set<AopTarget>> proxEntry : proxMap.entrySet()){
            Class<?> proxyClass  = proxEntry.getKey();
            Set<AopTarget> targetClassSet  = proxEntry.getValue();

            for (AopTarget aopTarget : targetClassSet){
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (targetMap.containsKey(aopTarget)){
                    targetMap.get(aopTarget).add(proxy);
                }else{

                    List<Proxy> proxyList  = new ArrayList<Proxy>();
                    proxyList.add(proxy);
                    targetMap.put(aopTarget,proxyList);
                }
            }
        }

        return targetMap;
    }
}
