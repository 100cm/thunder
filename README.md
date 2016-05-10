
##Thunder是什么?
一个微型mvc 框架 轻巧,快速为一体。可以用作快速开发原型。

首先向所有开源作者表示尊敬


感谢以下同学的帮助:


[王爵](http://www.github.com/biezhi)


[kevin1999](http://www.github.com/kevin1999)
##Thunder有哪些功能？

* 集成了轻量级的Aop 动态代理.
* 集成了轻量级Ioc 不用再为 new 对象的另外一半而烦躁( =new duixiang() )
* 路由型过滤器,无性能损耗,lamda表达式让你的代码时尚时尚更时尚
* 集成了轻量级伪ActiveRecord 。对service厌烦的同学在原型里和Service层说88吧！(虽然这样做并不是很好)
* Restful风格的路由。多路由自动生成。告别重复编程烦恼（目前还是残疾的 后续会优化）。
* 支持jdk1.6 (AOP 只支持1.8+) 以及更高版本

##最新&准备更新(2016-5-8)
* 解决了aop 和 路由冲突的问题
* 解决了资源路由冲突的bug
* 着手准备build型实体类的注解

##最新更新(2016-4-5)
* 修复了ioc 和aop 不能共用的问题
* 添加了一个restful接口(可以更快的生成路由功能)

##有问题反馈
在使用中有任何问题，欢迎反馈给我，可以用以下联系方式跟我交流

* 邮件351711778@qq.com
* QQ: 351711778
* 微信:  zjc351711778

##开始使用
首先clone此源码，可以打包成jar或者直接在项目中使用
创建一个maven webapp 项目 并且在xml中添加如下配置
```xml
    <servlet>
        <servlet-name>thunder</servlet-name>
        <servlet-class>com.thunder.web.ThunderDispatcher</servlet-class>
        <load-on-startup>1</load-on-startup>
        <init-param>
            <param-name>lighting</param-name>
            <param-value>App</param-value>
        </init-param>
    </servlet>

```
在根目录下创建App.java 并且继承lighing接口

```java
public class App implements Lightning{
    @Override
    public void init(Thunder thunder) {
        
     }
}
```
这样我们第一个thunder应用就完成啦！

##增加路由
我们的应用目前还没有任何路由配置 接下来我们去给应用添加资源以及路由


首先我们增加welcome路由
```java
  public class App implements Lightning{
    @Override
    public void init(Thunder thunder) {
      //这里会增加restful 7条路由
      //get -> /welcome  method:index
      //get -> /welcome/new  method:fresh
      //post -> /welcome/create  method:create
      //put  -> /welcome/:id/update method:update
      //get  -> /welcome/:id/edit   method:edit
      //delete  -> /welcome/:id/delete method:delete
      //get -> /welcome/:id   method:show
      
      //此资源调用welcom类下的方法
      thunder.addResource("welcome",new Welcome()); 
      
      //增加单独一条路由 执行welcome 类下index方法
      thunder.addRoute("/demo", Var.GET,"index",new Welcome());
     }
}
```
然后我们增加一条过滤路由
```java
      thunder.before("/demo",Var.GET,(request,response)->{
         System.out.print("我拦截demo啦！");
      });
```
也可以以大家熟悉的注解方式增加路由

```java

@Controller
public class test {

  //IOC 依赖注入
  @Inject
  Util util;

  @Action(value = "/demo",method = "GET")
  //Aop 拦截此方法
  @Check
  public void demo(Request request, Response response){
      System.out.print("hello world");
  }
}
```
##请求响应
看完以上 增加完路由之后 我们还要给它进行响应
首先我们有一张表单
```html
<form method="post" action="/welcome/create">
<input type="text" name="username" />
<button type="submit">submit</button>
</form>

```
对应的java Welcome.java  类
```java
public void create(Request request ,Response response){

        User user = (User)request.getModel(User.class);

        System.out.print(user.getUsername());

   }
```

当提交这个表单 会执行welcome类下的create方法

详见生成resource的注释。

#####如何跳转?

还是刚才那个方法
```java
public void create(Request request ,Response response){

        User user = (User)request.getModel(User.class);

        System.out.print(user.getUsername());
       
        //进行页面渲染
        response.render("/hello");
        
        //进行重定向
        response.redirect_to("/welcome/3");
        
        

   }
```

######获取参数呢？发送参数呢？

```java
//发送参数
request.sendParams("key",value);
//获取资源id
request.resourceId();
//获取参数
request.params("key");
request.paramsAsInt("key")

```


##IOC 使用

和spring使用方法大同小异
使用inject 注解即可

```java
//将demo注入容器
@Inject
 Demo demo
```

##Aop的使用

Aop 通俗理解为动态代理 再通俗一点。就是再执行某件事之前 我要先交给小弟先准备工具

自己再做这件事 如果小弟那边出错 我就不用做了。

首先我们要建立一个注解 当作判断切入点。

现在我们拥有一个@check 注解
```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Check {
}

```

现在我们要切入加过这个注解的方法

这里就是我们的小弟了
```java
/**要代理的注解类 或注解方法
* 
*/
@Aspect(value = Check.class)
public class Aoptest extends AspectProxy {

/**
 * 重写父类代理方法 对应前置增强 后置增强 环绕增强
 * @param c 对应的代理类
 * @param method 代理执行方法
 * @param params 参数
 * @throws Throwable
 */
public void before(Class<?> c , Method method, Object[] params)throws Throwable{

    System.out.print(" i am in proxy");

}

/**
 * 环绕增强 如果返回true 则进行前后增强  false则只执行被切入方法
 * @param c
 * @param method
 * @param params
 * @return
 * @throws Throwable
 */
public boolean intercept(Class<?> c ,Method method,Object[] params)throws Throwable{
    //返回
    return true;
}

/**
 * 
 * @param c
 * @param method
 * @param params
 * @param o 代理对象
 * @throws Throwable
 */
public void after(Class<?> c ,Method method,Object[] params , Object o)throws Throwable{


}
}
```

接下来我们在大哥上面加标记(@Check)

```java
@Controller
public class test {

  //IOC 依赖注入
  @Inject
  Util util;

  @Action(value = "/demo",method = "GET")
  //Aop 拦截此方法
  @Check
  public void demo(Request request, Response response){
      System.out.print("hello world");
  }
}
```

小弟执行顺序为 before -> 大哥方法 -> after 

 ##伪ActiveRecord
 首先我们先有一张user表
 
 
 表结构对应实体类User (名字要对应一致！！！！) 
 这也是约定>配置的最重要的一点

创建user类之后 我们就可以开始愉快的crud了

```java
  public static void main(String args[]){
      //数据库链接 这一部必须要加在初始化的thunder里。
      DB.open("com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1/test", "root", "root");
      User user = new User();
      user.setAge(3);
      user.setId(34);
      user.setPassword("");
      
      Model.select("id","username").list(User.class);
      
      Model.where("id","26").list(User.class);
      //id＝25并且根据id排序的user
      Model.where("id","25").orderBy("id").one(User.class);
      
      List<User> users = Model.find_by_sql("select * from user");
      
      Model.find_all(User.class);
      //id大于20的user
      Model.whereGt("id","20").list(User.class);
      
      Model.save(user);
      
      Model.where("id","28").update(user);
      
      //update的数据必须带id 否则会报错
      Model.update(user);
      
      //删除
      Model.delete(1,User.class);
  }
}
```

##其他





