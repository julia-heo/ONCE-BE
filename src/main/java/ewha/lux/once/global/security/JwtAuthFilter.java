package ewha.lux.once.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ewha.lux.once.domain.user.dto.LoginResponseDto;
import ewha.lux.once.domain.user.service.RedisService;
import ewha.lux.once.global.common.ResponseCode;
import ewha.lux.once.global.common.UserAccount;
import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.common.ResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    public static final String HEADER_KEY = "Authorization";
    public static final String REFRESH_HEADER_KEY = "Authorization-refresh";
    public static final String PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    private final GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request, HEADER_KEY);
        String refreshToken = resolveToken(request, REFRESH_HEADER_KEY);

        if (accessToken != null && jwtProvider.validateToken(accessToken)) {
            // access token있고 유효할 때
            if(jwtProvider.isAccessTokenLoggedOut(accessToken)){
                // 블랙리스트
                sendErrorResponse(response,403,ResponseCode.BLACKLISTED_TOKEN);
                return;
            }

            // users 찾아 인증정보 저장
            Users users = jwtProvider.extractUsersFromToken(accessToken);
            saveAuthentication(users);

        } else if (refreshToken != null) {
            if (jwtProvider.validateToken(refreshToken)) {
                // refresh token있고 유효할 때

                Users users = jwtProvider.extractUsersFromToken(refreshToken);
                String redisRefreshToken = (String) redisService.getValue(REFRESH_TOKEN_PREFIX+users.getId().toString());

                if (redisRefreshToken != null && redisRefreshToken.equals(refreshToken)) {
                    // access token 재발급
                    String newAccessToken = jwtProvider.createAccessToken(users.getLoginId());

                    response.setHeader(HEADER_KEY, PREFIX + newAccessToken);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");
                    response.setStatus(HttpServletResponse.SC_OK);
                    LoginResponseDto loginResponseDto = new LoginResponseDto(users.getId(), newAccessToken, refreshToken);

                    ResponseDto<LoginResponseDto> responseDto1 = ResponseDto.response(1000, true,  "access token이 재발급되었습니다.", loginResponseDto);

                    PrintWriter writer = response.getWriter();
                    writer.write(new ObjectMapper().writeValueAsString(responseDto1));
                    writer.flush();

                    saveAuthentication(users);
                    return;

                } else {
                    // refresh token이 유효하지 않을 때
                    sendErrorResponse(response,403,ResponseCode.INVALID_REFRESH);
                    return;
                }
            } else {
                // refresh token이 만료되었을 때
                sendErrorResponse(response,403,ResponseCode.UNAUTHORIZED_REFRESH);
                return;
            }
        } else if (accessToken != null) {
            // access token이 유효하지 않을 때
            sendErrorResponse(response,403,ResponseCode.UNAUTHORIZED);
            return;
        }
        filterChain.doFilter(request, response);

    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, ResponseCode code) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();
        ResponseDto<?> responseDto = ResponseDto.response(code.getCode(), code.isInSuccess(),  code.getMessage());
        writer.write(new ObjectMapper().writeValueAsString(responseDto));
        writer.flush();
    }

    // Token만 분리해 반환 (Bearer 제거)
    public String resolveToken(HttpServletRequest request, String headerKey) {
        String bearerToken = request.getHeader(headerKey);
        if (bearerToken != null && bearerToken.startsWith(PREFIX)) {
            return bearerToken.substring(PREFIX.length());
        }
        return null;
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

}
