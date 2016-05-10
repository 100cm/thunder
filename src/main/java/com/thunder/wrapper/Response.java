package com.thunder.wrapper;

import com.thunder.core.Thunder;
import com.thunder.render.JspRender;
import com.thunder.render.Render;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by icepoint1999 on 2/29/16.
 */
public class Response {


    private  HttpServletResponse httpServletResponse;

    private Render render =null;

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    public Response(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
        this.render = Thunder.zeus().getRender();
    }

    public  void render(String name){

       render.render(name,null);

    }

    public void redirect_to(String path){
        try {
            httpServletResponse.sendRedirect(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderJSON(Object o){

        try {
            render.render(o,httpServletResponse.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renderJSON(Map<String ,Object> map){

        try {
            render.render(map,httpServletResponse.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendFile(String path) throws IOException {

        File f = new File(path);
        if(f.exists()){
            FileInputStream  fis = new FileInputStream(f);
            String filename= URLEncoder.encode(f.getName(),"utf-8"); //解决中文文件名下载后乱码的问题
            byte[] b = new byte[fis.available()];
            fis.read(b);
            httpServletResponse.setCharacterEncoding("utf-8");
            httpServletResponse.setHeader("Content-Disposition","attachment; filename="+filename+"");
            //获取响应报文输出流对象
            ServletOutputStream out = httpServletResponse.getOutputStream();
            //输出
            out.write(b);
            out.flush();
            out.close();
        }
    }
}
