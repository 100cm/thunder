package com.thunder.web;

import com.thunder.Constant;
import com.thunder.core.Lightning;
import com.thunder.core.Thunder;
import com.thunder.helper.BeanHelper;
import com.thunder.helper.HelpLoader;
import com.thunder.resources.Resource;
import com.thunder.resources.ResourceMatcher;
import com.thunder.resources.Resources;
import com.thunder.route.Route;
import com.thunder.route.RouteMatcher;
import com.thunder.route.Routes;
import com.thunder.util.*;
import com.thunder.wrapper.Request;
import com.thunder.wrapper.Response;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by icepoint1999 on 2/29/16.
 */
public class ThunderDispatcher extends HttpServlet {

    private Lightning lightning;

    private Thunder thunder;

    private ServletContext servletContext;

    private RouteMatcher routeMatcher = new RouteMatcher(new ArrayList<Route>());

    private ResourceMatcher resourceMatcher = new ResourceMatcher(new ArrayList<Resource>());

    private  org.apache.log4j.Logger LOGGER = LogManager.getLogger(ThunderDispatcher.class);


    public ThunderDispatcher() {
    }
    public ThunderDispatcher(Lightning lightning) {

        this.lightning = lightning;
        thunder.init();
    }

    public void init(ServletConfig servletConfig){
            Thunder zeus  = Thunder.zeus();
            if(!zeus.isInit){

                String className =  servletConfig.getInitParameter("lighting");
                Lightning lightning = this.getLighting(className);
                lightning.init(zeus);
                Routes routes =zeus.getRoutes();
                Resources resources = zeus.getResources();

                if(null != routes){
                    routeMatcher.setRouteList(routes.getRoutelist());
                    routes.getRoutelist().forEach(r->{
                        LOGGER.info(Constant.LOG_RS_NAME+"Add URL: " + r.getPath() + " Method: "+r.getMethod());
                    });
                }


                if(null != resources){
                    resourceMatcher.setResourceList(resources.getResourcesList());
                }

                servletContext = servletConfig.getServletContext();
                zeus.setIsInit(true);
            }
        HelpLoader.init();

    }

    public void service(HttpServletRequest httpServletRequest ,HttpServletResponse httpServletResponse){
        thunder = Thunder.zeus();
        try {
            httpServletRequest.setCharacterEncoding("UTF-8");
            httpServletResponse.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //请求的uri
             String uri =httpServletRequest.getServletPath();

             Resource resource = this.resourceMatcher.findResource(PathUtil.getResource(uri));

            Route route ;
        //优先判断是否属于资源。


                 if (resource !=null) {

                     route= routeMatcher.findRoute(PathUtil.matchResourceRoute(uri),httpServletRequest.getMethod());

                   }else{

                     route= routeMatcher.findRoute(uri,httpServletRequest.getMethod());
                 }

            if (route != null) {
                    // 实际执行方法
                 LOGGER.info(Constant.LOG_RS_NAME+"Action is : "+ uri + " Method is " + httpServletRequest.getMethod());
                 thunder.setPathVarianble(Util.restMap(route.getPath(),uri));
                 handle(httpServletRequest, httpServletResponse, route);

             }else{
                try {
                    httpServletResponse.sendError(404);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }



    public   void  handle(HttpServletRequest httpServletRequest ,HttpServletResponse httpServletResponse,Route route){

        Request request = new Request(httpServletRequest);

        Response response = new Response(httpServletResponse);
        // 初始化上下文
        ThunderContext.initContext(servletContext, request, response);

        Object controller = route.getController() ;
        // 要执行的路由方法
        Method actionMethod = route.getAction();
        // 执行route方法
        if(null!=routeMatcher.findRouteTarget(route.getPath(),route.getMethod(),"before")){
            Route route1 = routeMatcher.findRouteTarget(route.getPath(),route.getMethod(),"before");
            MethodUtil.doMethod(route1.getController(), route1.getAction(), request, response);
        }
        if(!response.getHttpServletResponse().isCommitted()){
            MethodUtil.doMethod(controller, actionMethod, request, response);
        }

        if(null!=routeMatcher.findRouteTarget(route.getPath(),route.getMethod(),"after")){
            Route route2 = routeMatcher.findRouteTarget(route.getPath(),route.getMethod(),"before");
            MethodUtil.doMethod(route2.getController(), route2.getAction(), request, response);
        }
    }

    private  Lightning  getLighting(String classname){

    if(null!=classname){

        try {
            Class<?> clazz = Class.forName(classname);
            Lightning lightning = (Lightning) clazz.newInstance();
            return lightning;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }
        throw new RuntimeException("init lightning class error!");
    }




}
