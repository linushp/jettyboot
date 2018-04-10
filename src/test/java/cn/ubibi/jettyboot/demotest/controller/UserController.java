package cn.ubibi.jettyboot.demotest.controller;

import cn.ubibi.jettyboot.demotest.controller.parser.CurrentUser;
import cn.ubibi.jettyboot.demotest.controller.parser.UserInfoParser;
import cn.ubibi.jettyboot.demotest.controller.render.PageRender;
import cn.ubibi.jettyboot.demotest.dao.UserDAO;
import cn.ubibi.jettyboot.demotest.entity.UserEntity;
import cn.ubibi.jettyboot.framework.commons.JBPage;
import cn.ubibi.jettyboot.framework.ioc.JBAutowired;
import cn.ubibi.jettyboot.framework.jdbc.model.JBUpdateResult;
import cn.ubibi.jettyboot.framework.rest.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.ubibi.jettyboot.framework.rest.annotation.JBGetMapping;
import cn.ubibi.jettyboot.framework.rest.annotation.JBPostMapping;
import cn.ubibi.jettyboot.framework.rest.annotation.JBRequestParams;
import cn.ubibi.jettyboot.framework.rest.ifs.JBRequestParser;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;


//@RestMapping(path = "/user")
public class UserController {

    private static Logger logger = Log.getLogger(UserController.class);

    @JBAutowired
    private UserDAO userDAO;


    @JBGetMapping(path = "/test_insert")
    public JBUpdateResult getmm21(@JBRequestParams UserInfoParser reqParser, JBRequest JBRequest) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("name","name" + System.currentTimeMillis() + "_" + Math.random());
        map.put("yaoli",123);
        map.put("dai",3);
        map.put("fid",3);
        map.put("mid",3);
        map.put("create_time",System.currentTimeMillis());
        map.put("update_time",System.currentTimeMillis());
       return userDAO.insertObject(map);
//        return  "123---" + reqParser.getName() +"=====" +
    }


    @JBGetMapping(path = "/test")
    public String getmm(UserInfoParser reqParser, JBRequest JBRequest, CurrentUser currentUser) throws Exception {
        new UserDAO().findAll();
        if(reqParser instanceof JBRequestParser){
            System.out.println("111");
        }
        return  "123---" + reqParser.getName() +"=====" + currentUser.getName();
    }



    @JBGetMapping(path = "/")
    public JBPage<UserEntity> getUserById3(JBRequest JBRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Integer pageSize = JBRequest.getRequestParam("pageSize","10").toInteger();
        Integer pageNo = JBRequest.getRequestParam("pageNo","0").toInteger();




//        logger.info("aaa");
        long t1 = System.currentTimeMillis();



        JBPage<UserEntity> result = userDAO.findPage(pageNo, pageSize);
//        return "hello222";

        long t2 = System.currentTimeMillis();

        logger.info("::"+(t2-t1));
        return result;
    }




    @JBGetMapping(path = "/:uid")
    public Object getUserById(JBRequest params, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String uid =  params.getPathVariable("uid").toString();
        String name = params.getRequestParam("name").toString();
        String[] names = request.getParameterValues("name");


//        System.out.println("111");
//        try {
//            Thread.sleep(1000 * 10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//        request.getSession(true).setAttribute("uid",uid);
//        Object mm = request.getSession(true).getAttribute("uid");

        Cookie cookie = new Cookie("aaa","a2333");
        response.addCookie(cookie);

        Cookie[] cookies = request.getCookies();

        String aaa = request.getContextPath();

        return new PageRender("getUserById",userDAO.findById(uid));

//        return userDAO.findById(uid);
//        return "hello:" + uid + ":" + name;
    }


    @JBPostMapping(path = "/new/:uid")
    public String getUserById2(JBRequest JBRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String aaa = request.getContextPath();
        return "123saaa";
    }



}
