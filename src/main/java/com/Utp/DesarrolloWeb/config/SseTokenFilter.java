package com.Utp.DesarrolloWeb.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Filtro que permite autenticar el endpoint SSE usando el token JWT enviado
 * como query param (?token=...), ya que el navegador no permite agregar
 * headers personalizados a un EventSource nativo.
 *
 * Solo actúa sobre /api/pedidos/notificaciones.
 * Para todas las demás rutas, el filtro no hace nada.
 */
public class SseTokenFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        // Solo aplica al endpoint SSE
        if (path.contains("/api/pedidos/notificaciones")) {
            String tokenParam = request.getParameter("token");

            if (tokenParam != null && !tokenParam.isBlank()) {
                // Envolvemos el request para inyectar el header Authorization
                HttpServletRequest wrappedRequest = new HttpServletRequestWrapper(request) {
                    @Override
                    public String getHeader(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return "Bearer " + tokenParam;
                        }
                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        if ("Authorization".equalsIgnoreCase(name)) {
                            return Collections.enumeration(
                                    Collections.singletonList("Bearer " + tokenParam)
                            );
                        }
                        return super.getHeaders(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {
                        return super.getHeaderNames();
                    }
                };

                filterChain.doFilter(wrappedRequest, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
