package com.markcollab.config;

import com.markcollab.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Lista de caminhos que este filtro deve IGNORAR COMPLETAMENTE.
    // O método shouldNotFilter verificará se a requisição é para uma dessas URLs.
    private static final List<String> PATHS_TO_EXCLUDE_FROM_JWT_FILTER = Arrays.asList(
            "/api/auth/register",
            "/api/auth/authenticate",
            "/api/mercadopago/webhook" // <--- AQUI ESTÁ A CHAVE: Adicione o endpoint do webhook
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        System.out.println("DEBUG JWT Filter: Avaliando URI: " + requestURI); // <--- NOVO LOG AQUI
        boolean shouldExclude = PATHS_TO_EXCLUDE_FROM_JWT_FILTER.contains(requestURI);
        System.out.println("DEBUG JWT Filter: Deve excluir (" + requestURI + ")? " + shouldExclude); // <--- NOVO LOG AQUI
        return shouldExclude;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Se shouldNotFilter retornar true, este método nem será chamado para a requisição.
        // Portanto, a lógica de verificação do JWT só ocorrerá para as requisições que precisam dela.

        final String authHeader = request.getHeader("Authorization");

        // Se não houver cabeçalho de autorização ou não começar com "Bearer ",
        // continue a cadeia de filtros.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username;

        try {
            username = jwtService.extractUsername(jwt);

            // Se o username for válido e não houver autenticação já no SecurityContext
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // Se o token for válido para o userDetails
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Crie um objeto de autenticação e o coloque no contexto de segurança
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null, // Não temos credenciais aqui, pois o token já é a credencial
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Erro na validação do Token JWT: Token expirado. " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Token expirado");
            return;
        } catch (MalformedJwtException | SignatureException e) {
            System.out.println("❌ Erro na validação do Token JWT: Token inválido ou assinatura corrompida. " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Token inválido");
            return;
        } catch (Exception e) {
            System.out.println("❌ Erro inesperado na validação do Token JWT: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            response.getWriter().write("Erro interno ao processar token");
            return;
        }
    }
}
