package com.mays.mtgboostergame.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mays.mtgboostergame.security.jwt.JwtRequestFilter;
import com.mays.mtgboostergame.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final JwtRequestFilter jwtRequestFilter;
    private final ObjectMapper mapper;

    @Bean
    public static RestTemplate restTemplate(RestTemplateBuilder builder) { return builder.build(); }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsFilter corsFilter() {
        CorsFilter filter = new CorsFilter();
        return filter;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                "/css/**",
                "/js/**",
                "/login",
                "/deck",
                "/userPage",
                "/api/user/login",
                "/api/user/create"
        )
                .and()
                .ignoring()
                .antMatchers(HttpMethod.OPTIONS);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(corsFilter(), SessionManagementFilter.class)
            .csrf()
                .disable()
                .headers()
                .frameOptions().disable()
            .and()
            .authorizeRequests()
                .antMatchers("/**").authenticated()
                .anyRequest().anonymous()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .logout()
                .permitAll()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    var timestamp = LocalDateTime.now(ZoneId.of("UTC"));
                    var status = HttpStatus.UNAUTHORIZED.value();
                    var error = HttpStatus.UNAUTHORIZED.getReasonPhrase();
                    var message = String.format("%d %s", status, error);
                    var path = request.getServletPath();
                    var err = new Error(timestamp, status, error, message, path);
                    var body = mapper.writeValueAsString(err);
                    response.getWriter().write(body);
                })
            .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)

        ;
    }

    private record Error(LocalDateTime timestamp, int status, String error, String message, String path) {}
}
