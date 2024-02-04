package ewha.lux.once.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.lux.once.domain.user.dto.LoginResponseDto;
import ewha.lux.once.global.common.UserAccount;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.ResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    public static final String HEADER_KEY = "Authorization";
    public static final String REFRESH_HEADER_KEY = "Authorization-refresh";
    public static final String PREFIX = "Bearer ";

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 자동 로그인 요청인 경우
        if(request.getRequestURI().equals("/user/auto")) {

            String accessToken = resolveToken(request, HEADER_KEY);
            String refreshToken = resolveToken(request, REFRESH_HEADER_KEY);

            if (!jwtProvider.validateAccessTokenExpiration(accessToken)) { // accesstoken이 유효한 경우
                Users users = jwtProvider.validateTokenAndGetUsers(accessToken);
                LoginResponseDto loginResponseDto = new LoginResponseDto(users.getId(), accessToken, refreshToken);

                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseEntity.ok(ResponseDto.response(1000,true, "access token이 검증되었습니다.", loginResponseDto))));
                return;
            } else if (!jwtProvider.validateAccessTokenExpiration(refreshToken)) { // accesstoken 만료, refreshtoken이 유효한 경우
                Users users = jwtProvider.validateTokenAndGetUsers(refreshToken);
                String newAccessToken = jwtProvider.generateAccessToken(users.getLoginId());
                LoginResponseDto loginResponseDto = new LoginResponseDto(users.getId(), newAccessToken, refreshToken);

                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseEntity.ok(ResponseDto.response(1000,true, "access token이 재발급되었습니다.", loginResponseDto))));
                return;
            } else { // refreshtoken 만료
                response.setContentType("application/json");
                response.setCharacterEncoding("utf-8");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseEntity.ok(ResponseDto.response(1000, true, "refresh token이 만료되었습니다. 다시 로그인해주세요"))));
                return;
            }
        }

        String accessToken = resolveToken(request, HEADER_KEY);

        if(StringUtils.hasText(accessToken) && jwtProvider.validateToken(accessToken)){
            Users users = jwtProvider.validateTokenAndGetUsers(accessToken);

            saveAuthentication(users);
            filterChain.doFilter(request, response);
            return;
        }



        filterChain.doFilter(request, response);
    }
    public void saveAuthentication(Users users) {
        UserAccount userAccount = new UserAccount(users);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userAccount,
                userAccount.getPassword(),
                authoritiesMapper.mapAuthorities(userAccount.getAuthorities()));

        // context 초기화 후 인증 정보 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // Token만 분리해 반환
    private String resolveToken(HttpServletRequest request, String key){
        String token = request.getHeader(key);

        // key의 헤더가 존재하고, Bearer로 시작한다면
        if(!ObjectUtils.isEmpty(token) && token.startsWith(PREFIX)) {
            return token.substring(PREFIX.length());
        }

        return null;
    }

}
