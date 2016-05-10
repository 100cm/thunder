package com.thunder.core;

import com.thunder.helper.BeanHelper;
import com.thunder.render.JspRender;
import com.thunder.render.Render;
import com.thunder.resources.Resources;
import com.thunder.route.Route;
import com.thunder.route.RouteHandler;
import com.thunder.route.RouteMatcher;
import com.thunder.route.Routes;
import com.thunder.util.ConfigLoader;
import com.thunder.wrapper.Request;
import com.thunder.wrapper.Response;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by icepoint1999 on 2/29/16.
 */
public final class Thunder {


    private static final Logger LOGGER = Logger.getLogger(Thunder.class.getName());

//    存放所有路由
    private Routes routes;

//    存放所有restful资源

    private Resources resources;

//    配置加载

    private ConfigLoader configLoader;

    //加载渲染器

    private Render render;

    //资源变量

    public Map<String, String> getPathVarianble() {
        return pathVarianble;
    }

    public void setPathVarianble(Map<String, String> pathVarianble) {
        this.pathVarianble = pathVarianble;
    }

    private Map<String,String> pathVarianble;


    private String appBasePackage;

    public String getAppBasePackage() {
        return null==appBasePackage? "" :appBasePackage;
    }

    public void setAppBasePackage(String appBasePackage) {
        this.appBasePackage = appBasePackage;
    }

    public boolean isInit = false;

    public void init(){

            if(this.isInit==false){
                this.isInit=true;
            }
    }

    private Thunder(){
        routes = new Routes();
        resources =new Resources();
        configLoader = new ConfigLoader();
        render = new JspRender();
    }

    public void setIsInit(boolean isInit) {
        this.isInit = isInit;
    }

    //单例模式 单例Thunder类 全局只有一个对象

    private static class ThunderSingle{

        private static Thunder ZEUS = new Thunder();
    }

    public static Thunder zeus(){

            return ThunderSingle.ZEUS;

    }

    public Thunder loadConfig(String conf){

        configLoader.load(conf);
        return this;
    }

    public Thunder setConf(String name, String value){
        configLoader.setConf(name, value);
        return this;
    }

    public String getConf(String name){
        return configLoader.getConf(name);
    }

    public Thunder addGetRoute(String path ,String methodName ,Object controller){

        try {
            Method method =controller.getClass().getMethod(methodName, Request.class , Response.class);
            controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
            this.routes.addRoute(path,Var.GET, method,controller);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return this;


    }

    public Thunder addPostRoute(String path ,String methodName ,Object controller){

        try {
            Method method =controller.getClass().getMethod(methodName, Request.class , Response.class);
            controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
            this.routes.addRoute(path,Var.POST, method,controller);
        }
         catch (NoSuchMethodException e) {
           LOGGER.info("uri:"+path +"-->"+Var.GET);
        }
        return this;
    }

    public Thunder addDeleteRoute(String path ,String methodName ,Object controller){
        try {
            Method method =controller.getClass().getMethod(methodName, Request.class , Response.class);
            controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
            this.routes.addRoute(path,Var.DELETE, method,controller);
        } catch (NoSuchMethodException e) {

        }
        return this;
    }

    public Thunder addPutRoute(String path ,String methodName ,Object controller){

        try {
            Method method =controller.getClass().getMethod(methodName, Request.class , Response.class);
            controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
            this.routes.addRoute(path,Var.PUT, method,controller);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return this;
    }

    public Thunder addPatchRoute(String path ,String methodName ,Object controller){

        try {
            Method method =controller.getClass().getMethod(methodName, Request.class , Response.class);
            controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
            this.routes.addRoute(path,Var.PATCH, method,controller);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return this;


    }

    public Thunder addRoute(String path ,String method, String action ,Object controller){

        try {
            Method methodAction =controller.getClass().getMethod(action, Request.class , Response.class);
            controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
            this.routes.addRoute(path,  method, methodAction ,controller);

          }
            catch (NoSuchMethodException e) {
               e.printStackTrace();
        }

         return this;


    }

    public Thunder addResource(String name ,Object controller)  {

         this.resources.addResource(name,controller);
        controller = null== BeanHelper.getBean(controller.getClass())? controller : BeanHelper.getBean(controller.getClass());
        try {

            Method index =controller.getClass().getMethod("index", Request.class , Response.class);
            String index_path = "/"+name;
            this.routes.addRoute(index_path, Var.GET, index, controller);
        }
             catch (NoSuchMethodException e) {

         }
        Method show = null;
        try {
            show = controller.getClass().getMethod("show", Request.class , Response.class);
            String show_path  = "/"+name +"/:id";
            this.routes.addRoute(show_path, Var.GET, show, controller);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        Method edit = null;
        try {
            edit = controller.getClass().getMethod("edit", Request.class , Response.class);
            String edit_path  = "/"+name +"/:id/edit";
            this.routes.addRoute(edit_path, Var.GET, edit, controller);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        Method create = null;
        try {
            create = controller.getClass().getMethod("create", Request.class , Response.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        String create_path = "/"+name +"/create";
            this.routes.addRoute(create_path, Var.POST, create, controller);
        Method update = null;
        try {
            update = controller.getClass().getMethod("update", Request.class , Response.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        String update_path = "/"+name +"/:id/update";
            this.routes.addRoute(update_path, Var.PUT, update, controller);
            this.routes.addRoute(update_path, Var.PATCH, update, controller);
        Method fresh = null;
        try {
            fresh = controller.getClass().getMethod("fresh", Request.class , Response.class);
        } catch (NoSuchMethodException e) {
           LOGGER.info("CAN NOT FOUND This Method");
        }
        String fresh_path = "/"+name +"/new";
            this.routes.addRoute(fresh_path, Var.GET, fresh, controller);
        Method delete = null;
        try {
            delete = controller.getClass().getMethod("delete", Request.class , Response.class);
        } catch (NoSuchMethodException e) {
           LOGGER.info("can not find this function");
        }
        String delete_path = "/"+name +"/:id/delete";
            this.routes.addRoute(delete_path, Var.DELETE, delete, controller);
        return this;

    }


    public Thunder before(String path,String method,RouteHandler routeHandler){
        RouteMatcher routeMatcher = new RouteMatcher(this.routes.getRoutelist());
        Route route = routeMatcher.findRoute(path,method);
        Route route1 = new Route();
        route1.setMethod(route.getMethod());
        route1.setTarget("before");
        route1.setController(routeHandler);
        route1.setPath(route.getPath());
        try {
            route1.setAction(RouteHandler.class.getMethod("handle", Request.class, Response.class));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        this.routes.addRoute(route1);
        return this;

    }

    public Routes getRoutes() {
        return routes;
    }

    public void setRoutes(Routes routes) {
        this.routes = routes;
    }
    public Thunder addRoutes(Routes routes){
        this.routes.addRoute(routes.getRoutelist());
        return this;
    }

    public Render getRender() {
        return render;
    }

    public void setRender(Render render) {
        this.render = render;
    }

    public Resources getResources() {
        return resources;
    }

    public void setResources(Resources resources) {
        this.resources = resources;
    }
}
