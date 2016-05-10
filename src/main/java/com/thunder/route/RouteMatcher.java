package com.thunder.route;

import com.thunder.util.PathUtil;
import com.thunder.util.Util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by icepoint1999 on 2/29/16.
 *
 * 路由匹配类 用于匹配并且放入list
 */
public class RouteMatcher {

    private List<Route> routeList;

    public List<Route> getRouteList() {
        return routeList;
    }

    public void setRouteList(List<Route> routeList) {
        this.routeList = routeList;
    }

    public RouteMatcher(List<Route> routeList) {
        this.routeList = routeList;
    }


//    根据path查找路由  返回查询到得路由

  public Route findRoute(String path,String method){

        List<Route> matchRoutes =  new ArrayList<Route>();

      for(Route route :this.routeList) {
          if (PathUtil.matchesPath(path, route.getPath()) && method.equals(route.getMethod())) {
              matchRoutes.add(route);
              giveMacth(path, method, matchRoutes);
              return matchRoutes.size() > 0 ? matchRoutes.get(0) : null;
          }
      }
      for(Route route :this.routeList) {
            {
             Route route1 = PathUtil.findRestRoute(route,path,method);
              if (null == route1){
                  continue;
              }else{
                  return  route1;
              }

          }

      }
       return null;

  }
    //优先匹配原则



    public Route findRouteTarget(String path,String method,String target){

        List<Route> matchRoutes =  new ArrayList<Route>();

        for(Route route :this.routeList){

            if(PathUtil.matchesPath(path,route.getPath())&&method.equals(route.getMethod())&&target.equals(route.getTarget())){

                matchRoutes.add(route);
            }
        }
        //优先匹配原则
        giveMacth(path,method,target,matchRoutes);

        return matchRoutes.size() > 0 ? matchRoutes.get(0) : null;

    }

//    优先排序
    private void giveMacth(final String uri,final String method, List<Route> routes){

        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                if(o2.getPath().equals(uri)&&o2.getMethod().equals(method)){

                    return o2.getPath().indexOf(uri);
                }
                return -1;
            }
        });

    }

    //    优先排序
    private void giveMacth(final String uri,final String method,final String target, List<Route> routes){

        Collections.sort(routes, new Comparator<Route>() {
            @Override
            public int compare(Route o1, Route o2) {
                if(o2.getPath().equals(uri)&&o2.getMethod().equals(method)&&o2.equals(target)){

                    return o2.getPath().indexOf(uri);
                }
                return -1;
            }
        });

    }

}
