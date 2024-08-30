package statisticsservice.global.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import statisticsservice.global.security.filter.JwtAuthorizationFilter;
import statisticsservice.global.security.handler.AccountAccessDeniedHandler;
import statisticsservice.global.security.handler.AccountAuthenticationEntryPoint;
import statisticsservice.global.security.jwt.JwtProcessor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProcessor jwtProcessor;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/dailyStats/topBoards").permitAll()
                        .requestMatchers(HttpMethod.GET, "/weeklyStats/topBoards").permitAll()
                        .requestMatchers(HttpMethod.GET, "/monthlyStats/topBoards").permitAll()
                        .requestMatchers(HttpMethod.POST, "/generate/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/generate/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                );


        http
                .addFilter(new JwtAuthorizationFilter(authenticationManager, jwtProcessor));

        http
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(new AccountAccessDeniedHandler())
                        .authenticationEntryPoint(new AccountAuthenticationEntryPoint()));

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityConfig() {
        return (web -> web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()));
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
