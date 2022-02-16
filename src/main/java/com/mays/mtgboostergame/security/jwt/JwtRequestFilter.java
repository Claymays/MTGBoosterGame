package com.mays.mtgboostergame.security.jwt;

import com.mays.mtgboostergame.user.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserService userService;


    public JwtRequestFilter(JwtUtil jwtUtil,
                            UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        var requestTokenHeader = request.getHeader("Authorization");
        if (requestTokenHeader != null) {
            String token = null;
            String username = null;

            var bearer = "bearer";
            if (requestTokenHeader.startsWith(bearer)) {
                token = requestTokenHeader.substring(bearer.length());
                try {
                    username = jwtUtil.getUsernameFromToken(token);
                } catch(IllegalArgumentException e) {
                    log.error("unable to get jwt token", e);
                } catch(ExpiredJwtException e) {
                    log.warn("JWT token has expired", e);
                } catch(JwtException e) {
                    log.warn(e.getMessage());
                }
            } else {
                log.warn("JWT token does not begin with '" + bearer + "'");
            }

            var securityContext = SecurityContextHolder.getContext();
            if (username != null && securityContext.getAuthentication() == null) {
                var userDetails = userService.loadUserByUsername(username);

                if (jwtUtil.validateToken(token, userDetails)) {
                    var usernamePasswordAuthToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    var details = new WebAuthenticationDetailsSource().buildDetails(request);
                    usernamePasswordAuthToken.setDetails(details);

                    securityContext.setAuthentication(usernamePasswordAuthToken);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}
