package com.example.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// Bloqueia o acesso às telas do sistema para quem não fez login
public class LoginInterceptor implements HandlerInterceptor {

    public static final String USUARIO_SESSAO = "usuarioLogado";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var session = request.getSession(false);
        if (session != null && session.getAttribute(USUARIO_SESSAO) != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
