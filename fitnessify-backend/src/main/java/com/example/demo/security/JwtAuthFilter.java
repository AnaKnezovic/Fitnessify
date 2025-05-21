package com.example.demo.security;

import java.io.IOException;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.model.Korisnik;
import com.example.demo.repository.KorisnikRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtService jwtService;

    @Autowired
    private KorisnikRepository userRepository;

    @Override
    protected void doFilterInternal(@SuppressWarnings("null") HttpServletRequest request,
                                    @SuppressWarnings("null") HttpServletResponse response,
                                    @SuppressWarnings("null") FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        try {
            if (token != null) {
                email = jwtService.getEmailFromToken(token);

                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    Korisnik korisnik = userRepository.findByEmail(email).orElse(null);
                    if (korisnik != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        Collections.emptyList() // Ovdje možeš dodati role/authorities ako treba
                                );
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("JWT autentikacija OK za korisnika: {}", email);
                    } else {
                        logger.warn("JWT validan, ali korisnik ne postoji u bazi: {}", email);
                    }
                }
            }
        } catch (Exception ex) {
            logger.warn("JWT validacija nije prošla: {}", ex.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
