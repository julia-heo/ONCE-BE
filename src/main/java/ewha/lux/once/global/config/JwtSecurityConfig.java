package ewha.lux.once.global.config;

import ewha.lux.once.domain.user.service.RedisService;
import ewha.lux.once.global.security.JwtAuthFilter;
import ewha.lux.once.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public class JwtSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Override
    public void configure(HttpSecurity http) throws Exception{
        http.addFilterBefore(new JwtAuthFilter(jwtProvider, redisService), UsernamePasswordAuthenticationFilter.class);
    }
}
