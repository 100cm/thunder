package com.thunder.wrapper;


import com.thunder.core.Thunder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Field;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by icepoint1999 on 2/29/16.
 */
public  class Request {

    private HttpServletRequest servletRequest;


    public HttpServletRequest getServletRequest() {
        return servletRequest;
    }

    public void setServletRequest(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public Request(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public String params(String name) {

        return servletRequest.getParameter(name);

    }

    public int paramsAsInt(String name) {

        return Integer.parseInt(servletRequest.getParameter(name));

    }

    public String pathVariable(String name) {
        Thunder thunder = Thunder.zeus();
        return thunder.getPathVarianble().get(name);

    }

    public <T> T getModel(Class<?> c) {

        @SuppressWarnings("unchecked")
        Map<String, Object[]> map = servletRequest.getParameterMap();
        Field filed = null;
        Object object = null;
        try {
            object = c.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        for (String key : map.keySet()) {
            String variable = key;
            try {
                filed = c.getDeclaredField(variable);
                filed.setAccessible(true);
                Type type = filed.getType();
                System.out.print(type.getTypeName());
                if (type.getTypeName().equals("int")) {
                    filed.set(object, Integer.parseInt(String.valueOf(map.get(key)[0])));
                } else if (type.getTypeName().equals("java.lang.String")) {
                    filed.set(object, map.get(key)[0]);
                }

            } catch (NoSuchFieldException e) {
                continue;
            } catch (IllegalAccessException e) {
                continue;
            }


        }


        return (T) object;

    }

    public void sendParams(String name, Object o) {
        servletRequest.setAttribute(name, o);
    }


    public Object getSession(String name) {

        return servletRequest.getSession().getAttribute(name);

    }

    public void setSession(String name, Object o) {

        servletRequest.getSession().setAttribute(name, o);

    }

    public String getUri() {

        return servletRequest.getRequestURI();

    }

    /**
     * 上传文件
     * @param path
     * @throws IOException
     */
    public String saveFile(String path) throws IOException {

        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(1024*1024*20);
        File file = new File(servletRequest.getRealPath(path));
        if (!file.exists()){
            file.mkdir();
        }
        factory.setRepository(file);
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setSizeMax(1024*1024*20);
        List items = null;
        try {
            items = upload.parseRequest(servletRequest);
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        String filePath ="";
        for(Iterator it=items.iterator(); it.hasNext();) {
            FileItem item = (FileItem) it.next();
            String fileName = item.getName()==null? "" :item.getName();
            if (!fileName.equals("")){
                //获得文件类型
                String contentType = item.getContentType();
                FileOutputStream fos = new FileOutputStream(file.getPath()+"/" + System.currentTimeMillis() +
                        fileName);
                filePath = file.getPath()+"/" + System.currentTimeMillis() +
                        fileName;
                if (item.isInMemory()) {
                    fos.write(item.get());
                } else {
                    InputStream is = item.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = is.read(buffer)) > 1) {
                        fos.write(buffer, 0, len);
                    }
                    is.close();
                    fos.close();
                }
            }
            }

        return filePath;
        }



}
