package com.springw6.backend.configuration;

import com.springw6.backend.jwt.AccessDeniedHandlerException;
import com.springw6.backend.jwt.AuthenticationEntryPointException;
import com.springw6.backend.jwt.TokenProvider;
import com.springw6.backend.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class SecurityConfiguration implements WebMvcConfigurer {
   @Value("${jwt.secret}")
   String SECRET_KEY;
   private final TokenProvider tokenProvider;
   private final UserDetailsServiceImpl userDetailsService;
   private final AuthenticationEntryPointException authenticationEntryPointException;
   private final AccessDeniedHandlerException accessDeniedHandlerException;

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }


//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        // h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
//        return (web) -> web.ignoring()
//                .antMatchers("/h2-console/**")
//                .antMatchers("/api/members/**")
//                .antMatchers("/favicon.ico");
//
//    }


   @Bean
   @Order(SecurityProperties.BASIC_AUTH_ORDER)
   public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http
              .cors().configurationSource(corsConfigurationSource());

      http.csrf().disable()
              .authorizeRequests()
              .requestMatchers(CorsUtils::isPreFlightRequest).permitAll().and()

              .exceptionHandling()
              .authenticationEntryPoint(authenticationEntryPointException)
              .accessDeniedHandler(accessDeniedHandlerException)

              .and()
              .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              .and()
              .headers()
              .frameOptions().sameOrigin()

              .and()
              .authorizeRequests()
              .antMatchers("/members/**").permitAll()
              .antMatchers("/post/**").permitAll()
              .antMatchers("/comment/**").permitAll()
              .antMatchers("/main/**").permitAll()
              .antMatchers("/subComment/**").permitAll()
              .antMatchers("/likes/**").permitAll()
              .antMatchers("/upload/**").permitAll()
              .antMatchers("/kakao/**").permitAll()
              .antMatchers("/h2-console/**").permitAll()
              .anyRequest().authenticated()


              .and()
              .apply(new JwtSecurityConfiguration(SECRET_KEY, tokenProvider, userDetailsService));

      return http.build();

   }

   @Bean
   public CorsConfigurationSource corsConfigurationSource() {
      final CorsConfiguration configuration = new CorsConfiguration();

      configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://3.37.127.16:8080"));
      configuration.addAllowedHeader("*");
      configuration.addAllowedHeader("Authorization");
      configuration.addAllowedMethod("*");
      configuration.setAllowCredentials(true);
      configuration.setMaxAge(3600L); //preflight 결과를 1시간동안 캐시에 저장
      configuration.addExposedHeader("*");
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      source.registerCorsConfiguration("/**", configuration);
      return source;
   }

}
