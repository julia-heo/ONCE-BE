package ewha.lux.once.global.security;

import ewha.lux.once.domain.user.entity.Users;
import ewha.lux.once.global.repository.UsersRepository;
import ewha.lux.once.domain.user.service.UserService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    private static final long ACCESS_EXPIRE_TIME = 1000l * 60 * 60 * 2; // 2시간
    private static final long REFRESH_EXPIRE_TIME = 1000l * 60 * 60 * 24 * 14; // 2주
    private final UsersRepository usersRepository;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    @Getter
    @Value("${spring.jwt.secret}")
    private String secretKey;

    // 토큰 생성
    public String generateToken(String loginId,Long accessTokenValidTime,boolean isAccessToken) {
        Claims claims = Jwts.claims().setSubject(loginId);
        String subject = isAccessToken ? "access" : "refresh";

        claims.put("authority", subject); // 권한

        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenValidTime); // 만료 시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, secretKey) // (비밀키, 해싱 알고리즘)
                .compact();
    }
    // 액세스 토큰 생성
    public String generateAccessToken(String loginId) {
        return generateToken(loginId, ACCESS_EXPIRE_TIME,true);
    }

    // 리프레쉬 토큰 생성
    public String generateRefreshToken(String loginId) {
        return generateToken(loginId, REFRESH_EXPIRE_TIME,false);
    }

    // 토큰 유효성 확인, t/f 반환
    public boolean validateToken(String token) throws ExpiredJwtException {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        Date now = new Date();

        if (expiration.before(now)) {
            return false;
        }

        return true;
    }

    // 토큰 검증 및 payload 추출 (토큰으로 현재 Users 찾아 반환)
    public Users validateTokenAndGetUsers(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        String loginId = claims.getSubject();
        return usersRepository.findByLoginId(loginId).get();
    }


    // 토큰 만료 여부 확인
    public boolean validateAccessTokenExpiration(String  token) {
        try{
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            return claims.getBody().getExpiration().before(new Date()); // 현재보다 만료가 이전인지 확인
        }
        catch (ExpiredJwtException ignored){
            return  true;
        }
    }

    // accessToken 값 가져오기
    public String resolveAccessToken(HttpServletRequest request) {
        String token = request.getHeader(JwtProvider.HEADER_STRING);
        if (StringUtils.hasText(token) && token.startsWith(JwtProvider.TOKEN_PREFIX)) {
            return token.replace(JwtProvider.TOKEN_PREFIX, "");
        }
        return null;
    }

    // accessToken 만료 시간 반환
    public Long getExpiration(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        return claims.getExpiration().getTime();
    }
}
