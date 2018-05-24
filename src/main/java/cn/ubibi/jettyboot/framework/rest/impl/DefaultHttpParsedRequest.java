package cn.ubibi.jettyboot.framework.rest.impl;

import cn.ubibi.jettyboot.framework.commons.CollectionUtils;
import cn.ubibi.jettyboot.framework.commons.Constants;
import cn.ubibi.jettyboot.framework.commons.FrameworkConfig;
import cn.ubibi.jettyboot.framework.commons.IOUtils;
import cn.ubibi.jettyboot.framework.rest.ifs.HttpParsedRequest;
import com.alibaba.fastjson.JSON;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.security.Principal;
import java.util.*;

public class DefaultHttpParsedRequest implements HttpParsedRequest {

    protected String matchedControllerPath;
    protected HttpServletRequest httpServletRequest;
    protected Map<String, String> pathVariable;
    protected Map<String, Object> aspectVariable;
    protected byte[] _requestBody = null;


    public DefaultHttpParsedRequest(HttpServletRequest httpServletRequest, String matchedControllerPat) {
        this.httpServletRequest = httpServletRequest;
        this.matchedControllerPath = matchedControllerPat;
        this.pathVariable = parsePathVariable();
        this.aspectVariable = new HashMap<>();
    }


    @Override
    public String getPathVariable(String name) {
        return this.pathVariable.get(name);
    }


    @Override
    public String getCookieValue(String cookieName) {
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }


    @Override
    public void setAspectVariable(String name, Object obj) {
        aspectVariable.put(name, obj);
    }


    @Override
    public Object getAspectVariable(String name) {
        return aspectVariable.get(name);
    }


    @Override
    public Object getAspectVariable(Class<?> clazz) {
        Collection<Object> values = this.aspectVariable.values();
        for (Object obj : values) {
            if (obj.getClass() == clazz || clazz.isAssignableFrom(obj.getClass())) {
                return obj;
            }
        }
        return null;
    }


    @Override
    public byte[] getRequestBody() throws Exception {
        if (this._requestBody != null) {
            return this._requestBody;
        }


        int len = httpServletRequest.getContentLength();
        if (len <= 0) {
            return null;
        }
        if (len > FrameworkConfig.getInstance().getMaxRequestBodySize()) {
            throw new Exception("RequestBodyTooLarge");
        }

        ServletInputStream inputStream = httpServletRequest.getInputStream();

        byte[] buffer = IOUtils.inputStreamToByteArray(inputStream);

        inputStream.close();

        this._requestBody = buffer;
        return buffer;
    }


    @Override
    public String getRequestBodyAsString(Charset charset) throws Exception {
        byte[] bodyBytes = this.getRequestBody();
        if (bodyBytes == null) {
            return null;
        }
        return new String(bodyBytes, charset);
    }


    @Override
    public List<String> getParameterValuesAsList(String name) {
        String[] array = httpServletRequest.getParameterValues(name);
        if (CollectionUtils.isEmpty(array)) {
            return new ArrayList<>();
        }
        return CollectionUtils.toListFromArray(array);
    }


    @Override
    public <T> T getParameterValuesAsObject(Class<? extends T> clazz) {
        Map<String, Object> map2 = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();

            if (fieldType.isArray() || List.class.isAssignableFrom(fieldType)) {
                map2.put(fieldName, httpServletRequest.getParameterValues(fieldName));
            } else {
                map2.put(fieldName, httpServletRequest.getParameter(fieldName));
            }
        }

        String mapString = JSON.toJSONString(map2);
        T obj = JSON.parseObject(mapString, clazz);
        return obj;
    }


    @Override
    public String getMatchedControllerPath() {
        return this.matchedControllerPath;
    }


    //解析路径中的参数
    private Map<String, String> parsePathVariable() {
        String pathInfo = httpServletRequest.getPathInfo();
        String[] pathInfoArray = pathInfo.split(Constants.PATH_SPLIT);
        String[] targetPathArray = matchedControllerPath.split(Constants.PATH_SPLIT);

        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < targetPathArray.length; i++) {
            String p1 = targetPathArray[i];
            String p2 = pathInfoArray[i];

            /**
             * 支持两种形式
             * @see DefaultHttpPathComparator
             */
            if (p1.startsWith(":")) {
                String k = p1.replace(':', ' ');
                k = k.trim();
                map.put(k, p2);
            } else if (p1.startsWith("{") && p1.endsWith("}")) {
                String k = p1.replace('{', ' ').replace('}', ' ');
                k = k.trim();
                map.put(k, p2);
            }


        }
        return map;
    }


    /******************以下直接返回httpServletRequest中的内容*********/
    @Override
    public String getAuthType() {
        return httpServletRequest.getAuthType();
    }

    @Override
    public Cookie[] getCookies() {
        return httpServletRequest.getCookies();
    }

    @Override
    public long getDateHeader(String name) {
        return httpServletRequest.getDateHeader(name);
    }

    @Override
    public String getHeader(String name) {
        return httpServletRequest.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return httpServletRequest.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return httpServletRequest.getHeaderNames();
    }

    @Override
    public int getIntHeader(String name) {
        return httpServletRequest.getIntHeader(name);
    }

    @Override
    public String getMethod() {
        return httpServletRequest.getMethod();
    }

    @Override
    public String getPathInfo() {
        return httpServletRequest.getPathInfo();
    }

    @Override
    public String getPathTranslated() {
        return httpServletRequest.getPathTranslated();
    }

    @Override
    public String getContextPath() {
        return httpServletRequest.getContextPath();
    }

    @Override
    public String getQueryString() {
        return httpServletRequest.getQueryString();
    }

    @Override
    public String getRemoteUser() {
        return httpServletRequest.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String role) {
        return httpServletRequest.isUserInRole(role);
    }

    @Override
    public Principal getUserPrincipal() {
        return httpServletRequest.getUserPrincipal();
    }

    @Override
    public String getRequestedSessionId() {
        return httpServletRequest.getRequestedSessionId();
    }

    @Override
    public String getRequestURI() {
        return httpServletRequest.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return httpServletRequest.getRequestURL();
    }

    @Override
    public String getServletPath() {
        return httpServletRequest.getServletPath();
    }

    @Override
    public HttpSession getSession(boolean create) {
        return httpServletRequest.getSession(create);
    }

    @Override
    public HttpSession getSession() {
        return httpServletRequest.getSession();
    }

    @Override
    public String changeSessionId() {
        return httpServletRequest.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return httpServletRequest.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return httpServletRequest.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return httpServletRequest.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return httpServletRequest.isRequestedSessionIdFromUrl();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return httpServletRequest.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException {
        httpServletRequest.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
        httpServletRequest.logout();
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return httpServletRequest.getParts();
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return httpServletRequest.getPart(name);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return httpServletRequest.upgrade(handlerClass);
    }

    @Override
    public Object getAttribute(String name) {
        return httpServletRequest.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return httpServletRequest.getAttributeNames();
    }

    @Override
    public String getCharacterEncoding() {
        return httpServletRequest.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        httpServletRequest.setCharacterEncoding(env);
    }

    @Override
    public int getContentLength() {
        return httpServletRequest.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        return httpServletRequest.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return httpServletRequest.getContentType();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return httpServletRequest.getInputStream();
    }

    @Override
    public String getParameter(String name) {
        return httpServletRequest.getParameter(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return httpServletRequest.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        return httpServletRequest.getParameterValues(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return httpServletRequest.getParameterMap();
    }

    @Override
    public String getProtocol() {
        return httpServletRequest.getProtocol();
    }

    @Override
    public String getScheme() {
        return httpServletRequest.getScheme();
    }

    @Override
    public String getServerName() {
        return httpServletRequest.getServerName();
    }

    @Override
    public int getServerPort() {
        return httpServletRequest.getServerPort();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return httpServletRequest.getReader();
    }

    @Override
    public String getRemoteAddr() {
        return httpServletRequest.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
        return httpServletRequest.getRemoteHost();
    }

    @Override
    public void setAttribute(String name, Object o) {
        httpServletRequest.setAttribute(name, o);
    }

    @Override
    public void removeAttribute(String name) {
        httpServletRequest.removeAttribute(name);
    }

    @Override
    public Locale getLocale() {
        return httpServletRequest.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return httpServletRequest.getLocales();
    }

    @Override
    public boolean isSecure() {
        return httpServletRequest.isSecure();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return httpServletRequest.getRequestDispatcher(path);
    }

    @Override
    public String getRealPath(String path) {
        return httpServletRequest.getRealPath(path);
    }

    @Override
    public int getRemotePort() {
        return httpServletRequest.getRemotePort();
    }

    @Override
    public String getLocalName() {
        return httpServletRequest.getLocalName();
    }

    @Override
    public String getLocalAddr() {
        return httpServletRequest.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
        return httpServletRequest.getLocalPort();
    }

    @Override
    public ServletContext getServletContext() {
        return httpServletRequest.getServletContext();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return httpServletRequest.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return httpServletRequest.startAsync(servletRequest, servletResponse);
    }

    @Override
    public boolean isAsyncStarted() {
        return httpServletRequest.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return httpServletRequest.isAsyncSupported();
    }

    @Override
    public AsyncContext getAsyncContext() {
        return httpServletRequest.getAsyncContext();
    }

    @Override
    public DispatcherType getDispatcherType() {
        return httpServletRequest.getDispatcherType();
    }
}
