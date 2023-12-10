package com.cardpulse.jwt;

import com.cardpulse.service.implementation.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";
    @Autowired
    private JWTUtilService jwtUtilService;
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        final Optional<String> jwt = getJwtFromRequest(request);
        jwt.ifPresent(token -> {
            try {
                final String subject = jwtUtilService.getSubject(token);
                final UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(subject);

                if (jwtUtilService.isTokenValid(token, userDetails.getUsername()) &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    setSecurityContext(new WebAuthenticationDetailsSource().buildDetails(request),
                            userDetails);

                }
            }  catch (SignatureException |
                      MalformedJwtException |
                      ExpiredJwtException |
                      UnsupportedJwtException |
                      IllegalArgumentException e) {
                logger.error(e.getMessage());
            }
        });
        filterChain.doFilter(request, response);
    }

    private void setSecurityContext(WebAuthenticationDetails authDetails,
                                    UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authenticationToken.setDetails(authDetails);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private static Optional<String> getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER)) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }
}
