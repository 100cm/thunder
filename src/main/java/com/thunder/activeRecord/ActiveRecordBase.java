package com.thunder.activeRecord;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.thunder.Constant;
import com.thunder.util.ObjectUtil;
import com.thunder.util.Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by icepoint1999 on 3/14/16.
 */
public  abstract class ActiveRecordBase {



    public static  <T> T find_by_id(int id ,Class<T> tClass){
        String[] tablename =tClass.getName().toLowerCase().split("[.]");
        String table = tablename[tablename.length-1];
        List<T> list = (List<T>) DB.sql2o.beginTransaction().createQuery("select * from " + table+ " where id = " +id).executeAndFetch(tClass);
        return list.get(0);
    }

    public  static <T> List<T> find_by_sql(String sql){
        @SuppressWarnings("unchecked")
		List<T> list = (List<T>) DB.sql2o.beginTransaction().createQuery(sql).executeAndFetchTable().asList();
        return list;
    }

    public static ActiveRecord where(String params , String value){
        Connection connection = DB.sql2o.beginTransaction();
        List<WhereParams> list = new ArrayList<WhereParams>();
        WhereParams whereParams = new WhereParams(params,value,"=");
        list.add(whereParams);
        ActiveRecord activeRecord = new ActiveRecord();
        activeRecord.setConnection(connection);
        CustomSql customSql = new CustomSql();
        customSql.setWhere(list);
        activeRecord.setCustomSql(customSql);
        return activeRecord;
    }

    public static ActiveRecord whereGt(String key ,String value ){
        Connection connection = DB.sql2o.beginTransaction();
        List<WhereParams> list = new ArrayList<WhereParams>();
        WhereParams whereParams = new WhereParams(key,value,">");
        list.add(whereParams);
        ActiveRecord activeRecord = new ActiveRecord();
        activeRecord.setConnection(connection);
        CustomSql customSql = new CustomSql();
        customSql.setWhere(list);
        activeRecord.setCustomSql(customSql);
        return activeRecord;
    }

    public static ActiveRecord whereLt(String key ,String value ){
        Connection connection = DB.sql2o.beginTransaction();
        List<WhereParams> list = new ArrayList<WhereParams>();
        WhereParams whereParams = new WhereParams(key,value,"<");
        list.add(whereParams);
        ActiveRecord activeRecord = new ActiveRecord();
        activeRecord.setConnection(connection);
        CustomSql customSql = new CustomSql();
        customSql.setWhere(list);
        activeRecord.setCustomSql(customSql);
        return activeRecord;
    }

    public static ActiveRecord select(String... args){
        ActiveRecord activeRecord = new ActiveRecord();
        String select = "select ";
        int i = 0;
        for(String val :args){
            select += val+" ";
            if(i<args.length-1){
                select+= ", ";
            }
            i++;
        }
        CustomSql customSql = new CustomSql() ;
        customSql.setSelect(select+" from #table# ");
        activeRecord.setCustomSql(customSql);
        return activeRecord;
    }

    public static Object  save(Object object){
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        Connection connection = DB.sql2o.beginTransaction();
        Field[] fields = object.getClass().getDeclaredFields();
        StringBuffer key = new StringBuffer();
        StringBuffer value = new StringBuffer();
        String table = Util.getclassName(object.getClass()).toLowerCase();
        int i=0;
         for(Field field :fields){
             field.setAccessible(true);
             key.append(field.getName());
             try {
                 String v = null==field.get(object)?  "" : field.get(object).toString();
                 value.append("'"+v+"'");
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             }
             i++;
             if(i<fields.length){
                 key.append(",");
                 value.append(",");
             }
         }
        String sql = "insert into "+table + "("+key +") values ("+value +")";
        logger.info(Constant.LOG_AC_NAME+" execute sql " + sql);
        connection.createQuery(sql).executeUpdate().commit();
        return connection.getKey();
    }

    public ActiveRecord all(){
        String sql = "select * from ";
        ActiveRecord activeRecord = new ActiveRecord();
        CustomSql customSql = new CustomSql();
        customSql.setSql(sql);
        activeRecord.setCustomSql(customSql);
        return activeRecord;
    }

    public  static  <T> List<T> find_all(Class<T> tClass){
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        Connection connection = DB.sql2o.beginTransaction();
        String table = Util.getclassName(tClass).toLowerCase();
        String sql = "select * from "+table;
        logger.info(Constant.LOG_AC_NAME+"execute sql " + sql);
        List<T> list = connection.createQuery(sql).executeAndFetch(tClass);
        return list;
    }

    public static void update(Object object){
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        Map<String,Object> map = ObjectUtil.getFiledValue(object);
        String tablename = Util.getclassName(object.getClass()).toLowerCase();
        String sql = "update "+tablename + " set " ;
        String params ="";
        int i=0;
        for(String key : map.keySet()){
           if(null != map.get(key)&&key!="id"){
               params += key + "=" + "'"+map.get(key)+"'";
           }
            i++;
            if(null!= map.get(key)&&i < map.size()&&key!="id"){
                params += ",";
            }
        }
        sql += params + " where id = " + "'" +map.get("id")+"'";
//        int pos = sql.lastIndexOf(",");
//        sql = sql.substring(0,pos)+sql.substring(pos+1);
        logger.info(Constant.LOG_AC_NAME+"execute sql " + sql);
        DB.sql2o.beginTransaction().createQuery(sql).executeUpdate().commit();

    }

    public  static void delete(int id ,Class<?> c){
         String name = Util.getclassName(c).toLowerCase();
         String sql = "delete from "+ name + " where id = '" + id+"'";
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        logger.info(Constant.LOG_AC_NAME+" execute sql " + sql);
         DB.sql2o.beginTransaction().createQuery(sql).executeUpdate().commit();

    }
    public static  void delete_by_sql(String sql){
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        logger.info(Constant.LOG_AC_NAME+" execute sql " + sql);
        DB.sql2o.beginTransaction().createQuery(sql).executeUpdate().commit();
    }

    public static void update_by_sql(String sql){
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        logger.info(Constant.LOG_AC_NAME+" execute sql " + sql);
        DB.sql2o.beginTransaction().createQuery(sql).executeUpdate().commit();
    }

    public static Object insert_by_sql(String sql){
        Logger logger = LogManager.getLogger(ActiveRecordBase.class);
        logger.info(Constant.LOG_AC_NAME+" execute sql " + sql);
        return DB.sql2o.beginTransaction().createQuery(sql).executeUpdate().getKey();
    }






}
