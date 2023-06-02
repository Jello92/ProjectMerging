package com.example.zzan.global.util;

import com.example.zzan.global.exception.ExceptionEnum;
import com.example.zzan.global.security.UserDetailsServiceImpl;
import com.example.zzan.global.security.dto.TokenDto;
import com.example.zzan.global.security.entity.RefreshToken;
import com.example.zzan.global.security.repository.RefreshTokenRepository;
import com.example.zzan.user.entity.User;
import com.example.zzan.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String ACCESS_KEY = "ACCESS_KEY";
    public static final String REFRESH_KEY = "REFRESH_KEY";
    public static final String AUTHORIZATION_KEY = "auth";
    public static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TIME = 7 * 24 * 60 * 60 * 1000L;       // 테스트 기간 동안만 1주일로 설정 -> 테스트 이후에 1시간으로 변경해야함
    private static final long REFRESH_TIME = 7 * 24 * 60 * 60 * 1000L;

    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public TokenDto createAllToken(User user, UserRole role) {
        return new TokenDto(createToken(user, role, ACCESS_KEY), createToken(user, role, REFRESH_KEY));
    }

    public String resolveToken(HttpServletRequest request, String token) {
        String tokenName = token.equals(ACCESS_KEY) ? ACCESS_KEY : REFRESH_KEY;
        String bearerToken = request.getHeader(tokenName);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String createToken(User user, UserRole role, String type) {
        Date date = new Date();
        long time = type.equals(ACCESS_KEY) ? ACCESS_TIME : REFRESH_TIME;
        Map<String, Object> claim = new HashMap<>();
        claim.put("role",role);
        claim.put("id",user.getId());
        claim.put("email",user.getEmail());

        return BEARER_PREFIX
                + Jwts.builder()
                .setSubject(user.getEmail())
                .signWith(signatureAlgorithm, secretKey)
                .claim(AUTHORIZATION_KEY, claim)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + time))
                .compact();
    }
    //JWT 토큰의 만료시간
    public Long getExpiration(String accessToken){

        Date expiration = Jwts.parserBuilder().setSigningKey(secretKey)
                .build().parseClaimsJws(accessToken).getBody().getExpiration();

        long now = new Date().getTime();
        return expiration.getTime() - now;
    }

    public String validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return null;
        } catch (SecurityException | MalformedJwtException e) {
            return ExceptionEnum.INVALID_JWT_SIGNATURE.getMessage();
        } catch (ExpiredJwtException e) {
            return ExceptionEnum.ACCESS_TOKEN_NOT_FOUND.getMessage();
        } catch (UnsupportedJwtException e) {
            return ExceptionEnum.UNSUPPORTED_JWT_TOKEN.getMessage();
        } catch (IllegalArgumentException e) {
            return ExceptionEnum.EMPTY_JWT_CLAIMS.getMessage();
        }
    }

    public String getUserInfoFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public Authentication createAuthentication(String userId) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    public Boolean refreshTokenValidation(String token) {
        String validationError = validateToken(token);
        if (validationError != null){
            return false;
        }

        String userEmail = getUserInfoFromToken(token);
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findRefreshTokenByUserEmail(userEmail);

        return refreshToken.isPresent() && token.equals(refreshToken.get().getRefreshToken().substring(7));
    }

    public void setHeaderAccessToken(HttpServletResponse response, String accessToken) {
        response.setHeader(ACCESS_KEY, accessToken);
    }
}
