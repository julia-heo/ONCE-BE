package ewha.lux.once.global.security;

import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.repository.UsersRepository;
import org.springframework.data.redis.core.RedisTemplate;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private static final long ACCESS_EXPIRE_TIME = 1000l * 60 * 60 * 2; // 2시간
    private static final long REFRESH_EXPIRE_TIME = 1000l * 60 * 60 * 24 * 14; // 2주


    private final UsersRepository usersRepository;
    private final RedisTemplate redisTemplate;
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "blacklistAccessToken:";


    @Getter
    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String createAccessToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ACCESS_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }
    public String createRefreshToken(String userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_EXPIRE_TIME);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰으로 User찾아 반환
    public Users extractUsersFromToken(String token) {

        String loginId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        return usersRepository.findByLoginId(loginId).get();
    }

    // accessToken 만료 시간 반환
    public Long getExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        long expirationTime = claims.getExpiration().getTime();     // 만료 시간
        long currentTime = System.currentTimeMillis();              // 현재 시간
        return expirationTime - currentTime;                        // 남은 시간
    }


    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAccessTokenLoggedOut(String accessToken) {
        // Redis에서 액세스 토큰의 상태를 가져옴
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + accessToken;
        // 로그아웃 상태인지 확인
        return redisTemplate.hasKey(key);
    }
}
