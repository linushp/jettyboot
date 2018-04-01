package cn.ubibi.jettyboot.demotest.controller;

import cn.ubibi.jettyboot.demotest.dao.UserDAO;
import cn.ubibi.jettyboot.demotest.entity.UserEntity;
import cn.ubibi.jettyboot.framework.commons.PageData;
import cn.ubibi.jettyboot.framework.rest.RestGetMapping;
import cn.ubibi.jettyboot.framework.rest.RestMapping;
import cn.ubibi.jettyboot.framework.rest.RestParams;
import cn.ubibi.jettyboot.framework.rest.RestPostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;


@RestMapping(path = "/user")
public class UserController {

    private static Logger logger = Log.getLogger(UserController.class);

    private UserDAO userDAO = new UserDAO();


    @RestGetMapping(path = "/test")
    public String getmm() throws Exception {
        userDAO.findAll();
        return  "123";
    }


    @RestGetMapping(path = "/")
    public PageData<UserEntity> getUserById3(RestParams restParams, HttpServletRequest request, HttpServletResponse response) throws Exception {

        Integer pageSize = restParams.getRequestParam("pageSize","10").toInteger();
        Integer pageNo = restParams.getRequestParam("pageNo","0").toInteger();




//        logger.info("aaa");
        long t1 = System.currentTimeMillis();



        PageData<UserEntity> result = userDAO.findPage(pageNo, pageSize);
//        return "hello222";

        long t2 = System.currentTimeMillis();

        logger.info("::"+(t2-t1));
        return result;
    }




    @RestGetMapping(path = "/:uid")
    public Object getUserById(RestParams params, HttpServletRequest request, HttpServletResponse response) throws Exception {

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
        return userDAO.findById(uid);
//        return "hello:" + uid + ":" + name;
    }


    @RestPostMapping(path = "/new/:uid")
    public String getUserById2(RestParams restParams, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String aaa = request.getContextPath();
        return "123saaa";
    }



}
