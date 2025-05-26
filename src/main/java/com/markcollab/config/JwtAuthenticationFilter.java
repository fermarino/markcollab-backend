package com.markcollab.config;

import com.markcollab.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    private static final String[] PUBLIC_PATHS = {
            "/api/auth/register",
            "/api/auth/login",
            "/api/ia/gerar-descricao",
            "/api/email/enviar-contato",
            "/api/email/teste-debug"
    };

    private boolean isPublicPath(String path) {
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        System.out.println("🔍 Requisição recebida: " + path); // <-- log de depuração

        // Libera requisições públicas
        if (isPublicPath(path)) {
            System.out.println("✅ Rota pública liberada: " + path); // <-- log de liberação
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("⚠️ Requisição sem token ou token mal formatado.");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Token ausente ou inválido.");
            return;
        }

        try {
            String token = authHeader.substring(7); // Remove "Bearer "
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                System.out.println("✅ Autenticando usuário: " + username + " | Role: " + role);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, Collections.singletonList(() -> role)
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | MalformedJwtException | SignatureException e) {
            System.out.println("❌ Token JWT inválido: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Token inválido.");
        }
    }
}
