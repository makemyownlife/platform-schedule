package com.courage.platform.schedule.admin.mvc;

import com.courage.platform.schedule.admin.util.CookieUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by zhangyong on 2019/11/21
 */
@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (!(handler instanceof HandlerMethod)) {
            return super.preHandle(request, response, handler);
        }

        boolean needLogin = true;
        HandlerMethod method = (HandlerMethod) handler;

        String username = CookieUtil.getValue(request, "platformUsername");
        String token = CookieUtil.getValue(request, "platformToken");
        if (username == null || token == null) {
            PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
            if (permission != null) {
                needLogin = permission.limit();
            }
            if (needLogin) {
                response.sendRedirect(request.getContextPath() + "/toLogin");
                return false;
            }
        }

        return super.preHandle(request, response, handler);
    }


}
