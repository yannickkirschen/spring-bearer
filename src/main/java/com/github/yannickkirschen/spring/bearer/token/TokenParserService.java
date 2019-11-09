package com.github.yannickkirschen.spring.bearer.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * The {@link TokenParserService} allows parsing a token and getting the username out of it. It is intended to be used by every endpoint in order to get the
 * authenticated username. Please keep in mind that the exceptions are only catch in <code>JwtAuthorizationFilter</code>, since we don't need it in the public
 * endpoints.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Service
@SuppressWarnings("unused")
public class TokenParserService {
    private final TokenProperties tokenProperties;
    private final TokenCacheService tokenCacheService;

    @Autowired
    TokenParserService(TokenProperties tokenProperties, TokenCacheService tokenCacheService) {
        this.tokenProperties = tokenProperties;
        this.tokenCacheService = tokenCacheService;
    }

    /**
     * Parses the token.
     *
     * @param token The token to parse.
     *
     * @return The parsed token.
     */
    public Jws<Claims> parseToken(String token) {
        return Jwts.parser().setSigningKey(tokenProperties.getSecret().getBytes()).parseClaimsJws(token.replace("Bearer ", ""));
    }

    /**
     * Extracts the username out of the token.
     *
     * @param request The request to get the token from.
     *
     * @return The username in the token.
     */
    public String getUsername(HttpServletRequest request) {
        return tokenCacheService.getUsernameForToken(request.getHeader(tokenProperties.getHeader()).replace("Bearer ", ""));
    }
}
