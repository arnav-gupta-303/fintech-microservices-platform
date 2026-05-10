package com.fintech.api_gateway__services.filter;

import com.fintech.api_gateway__services.security.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {

        // Allow OPTIONS requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {

            chain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        log.info("Incoming request path: {}", path);

        // Public endpoints
        if (path.contains("/api/auth")) {

            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter()
                    .write("{\"error\": \"Missing token\"}");

            return;
        }

        String token = header.substring(7);

        // Validate JWT
        if (!jwtUtil.isTokenValid(token)) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter()
                    .write("{\"error\": \"Invalid token\"}");

            return;
        }

        // Extract authenticated email
        String email = jwtUtil.extractEmail(token);

        log.info("Authenticated user: {}", email);

        // Inject authenticated user header
        HttpServletRequestWrapper wrappedRequest =
                new HttpServletRequestWrapper(request) {

                    @Override
                    public String getHeader(String name) {

                        if ("X-Authenticated-User"
                                .equalsIgnoreCase(name)) {

                            return email;
                        }

                        return super.getHeader(name);
                    }

                    @Override
                    public Enumeration<String> getHeaders(
                            String name
                    ) {

                        if ("X-Authenticated-User"
                                .equalsIgnoreCase(name)) {

                            return Collections.enumeration(
                                    Collections.singletonList(email)
                            );
                        }

                        return super.getHeaders(name);
                    }

                    @Override
                    public Enumeration<String> getHeaderNames() {

                        var names =
                                Collections.list(
                                        super.getHeaderNames()
                                );

                        names.add("X-Authenticated-User");

                        return Collections.enumeration(names);
                    }
                };

        chain.doFilter(wrappedRequest, response);
    }
}
