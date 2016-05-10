package com.thunder.util;

import com.thunder.core.Thunder;
import com.thunder.core.Var;
import com.thunder.route.Route;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

/**
 * Created by icepoint1999 on 2/29/16.
 */
public class PathUtil {

    public static final String VAR_REGEXP = ":(\\w+)";
    public static final String VAR_REPLACE = "([^#/?]+)";

    private static final String SLASH = "/";

    public static  String getRelativePath(HttpServletRequest request){

            String path = request.getRequestURI();
            String contextPath = request.getContextPath();
            path = path.substring(contextPath.length());

            if (path.length() > 0) {

                path= path.substring(1);
            }
            if(!path.startsWith(SLASH)){

                path=SLASH+path;
            }

        try {
            path = URLDecoder.decode(path,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return path;

    }
//     在所有路由前+/
    public static String fixPath(String path) {
        if (path == null) {
            return "/";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        return path.replaceAll("[/]+", "/");
    }

    //返回标准路径
    private  static String parsePath(String path) {
        path = PathUtil.fixPath(path);

        URI uri = null;
        try {
            uri = new URI(path);

            return uri.getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }
//    比较路径是否一致

    public static boolean matchesPath(String routePath, String pathToMatch) {
        routePath = routePath.replaceAll(PathUtil.VAR_REGEXP, PathUtil.VAR_REPLACE);
        //忽略大小写进行匹配
        return pathToMatch.matches("(?i)" + routePath);
    }


    public  static String getViewPath(String view ){

        Thunder thunder = Thunder.zeus();

        String viewPrfix = thunder.getConf(Var.VIEW_PREFIX_FIELD);

        String viewSuffix =thunder.getConf(Var.VIEW_SUFFIX_FIELD);



        if (null == viewSuffix || viewSuffix.equals("")) {
            viewSuffix = Var.VIEW_SUFFIX;
        }
        if (null == viewPrfix || viewPrfix.equals("")) {
            viewPrfix = Var.VIEW_PREFIX;
        }
        String viewPath = viewPrfix+"/"+view;

        if (!view.endsWith(viewSuffix)) {
            viewPath += viewSuffix;
        }

        return viewPath.replaceAll("[/]+", "/");

        }

    public static  String getResource(String path){
        String resource = path.split("/")[1];

        return resource;
    }

    public static String matchResourceRoute(String path){

        String resouce =PathUtil.getResource(path);

        int length = path.split("/").length;

        String [] params = path.split("/");

        switch (length){
            case 2 :

               return path;

            case 3 :

                if(params[2].matches("[0-9]+")) {
                   return "/"+resouce + "/:id";
                }else if(params[2].equals("create")){

                    return "/"+resouce + "/create";
                }else if(params[2].equals("new")){

                    return "/"+resouce + "/new";
                }

            case 4:

                if(params[2].matches("[0-9]+")) {
                    return "/"+resouce + "/:id/"+params[3];
                }

            default: return path;
        }

    }


    public static String matchResourceMethod(String path){

        String resouce =PathUtil.getResource(path);

        int length = path.split("/").length;

        String [] params = path.split("/");

        switch (length){
            case 2 :
                return "index";
            case 3 :

                if(params[2].matches("[0-9]+")) {
                    return "show";
                }else if(params[2].equals("create")){

                    return "create";

                    }else if(params[2].equals("new")){
                    return "fresh";
                }

            case 4:

                if(params[2].matches("[0-9]+")&&params[3].equals("edit")) {
                    return "edit";
                }

                if(params[2].matches("[0-9]+")&&params[3].equals("delete")) {
                    return "delete";
                }

                if(params[2].matches("[0-9]+")&&params[3].equals("update")) {
                    return "update";
                }

            default: return null;
        }

    }

    public  static Route  findRestRoute(Route route,String path,String method){
        boolean flag = true ;
        String p  = route.getPath();
        String p_s =p.startsWith("/")==true ? p.substring(1,p.length()) :p;
        String path_s = path.startsWith("/")==true ? path.substring(1,path.length()) :path;
        String[] list = path_s.split("/");
        String[] routelist = p_s.split("/");
        if (method.equals(route.getMethod())){
            if (routelist.length == list.length) {
                for (int i = 0; i < list.length; i++) {
                    if (list[i].equals(routelist[i]) || routelist[i].startsWith(":")){
                        continue;
                    }else{
                        flag=false;
                        break;
                    }
                }
                return flag==false ? null : route;
            }else{

                return null;
            }
        }else{
            return null;

        }


    }




}
