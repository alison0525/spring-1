package com.back.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationFilter customAuthenticationFilter;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers("/favicon.ico").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/*/posts", "/api/*/posts/{id:\\d+}",
                                "/api/*/posts/{postId:\\d+}/comments", "/api/*/posts/{postId:\\d+}/comments/{commentId:\\d+}").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/members/login", "/api/v1/members/join").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/members/logout").permitAll()
                        .requestMatchers("/api/*/**").authenticated()
                        .anyRequest().authenticated())
                .csrf((csrf) -> csrf.disable())
                .headers((headers) -> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint((request, response, authenticationException) -> {
                                    response.setContentType("application/json");
                                    response.setStatus(401);
                                    response.getWriter().write(
                                            """
                                                        {
                                                            "resultCode": "401-1",
                                                            "msg": "로그인 후 이용해주세요."
                                                        }
                                                    """);
                                })
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                            response.setContentType("application/json");
                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    """
                                                                {
                                                                    "resultCode": "403-1",
                                                                    "msg": "권한이 없습니다."
                                                                }
                                                            """);
                                        }
                                ));


        return http.build();
    }
}
