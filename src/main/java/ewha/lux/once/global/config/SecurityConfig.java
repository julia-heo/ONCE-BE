package ewha.lux.once.global.config;

import ewha.lux.once.domain.user.service.RedisService;
import ewha.lux.once.global.security.JwtAuthFilter;
import ewha.lux.once.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

import java.util.List;



@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(c -> {
                        CorsConfigurationSource source = request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(List.of("*"));
//                            config.setAllowedOrigins(List.of("https://a.com", "https://b.com")); // 프론트
                            config.setAllowedMethods(List.of("*"));
                            config.setAllowedHeaders(List.of("Authorization","Authorization-refresh"));
                            return config;
                        };
                        c.configurationSource(source);
                    }
            )
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement((sessionManagement) ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(httpRequests -> httpRequests
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .requestMatchers(new AntPathRequestMatcher("/")
                            , new AntPathRequestMatcher("/css/**")
                            , new AntPathRequestMatcher("/images/**")
                    ).permitAll()
                    .requestMatchers("/user/signup").permitAll()
                    .requestMatchers("/user/duplicate").permitAll()
                    .requestMatchers("/user/login").permitAll()
                    .requestMatchers("/user/auto").permitAll()
                    .requestMatchers("/user/card/search").permitAll()
                    .requestMatchers("/user/logout").permitAll()
                    .requestMatchers("/user/card/**").permitAll()
                    .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtAuthFilter(jwtProvider, redisService), UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }


}
