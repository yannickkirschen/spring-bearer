package com.github.yannickkirschen.spring.bearer;

import com.github.yannickkirschen.spring.bearer.token.TokenCacheService;
import com.github.yannickkirschen.spring.bearer.token.TokenParserService;
import com.github.yannickkirschen.spring.bearer.token.TokenProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link JwtAuthorizationFilter} filters every incoming request and checks if the token is valid.
 *
 * @author Yannick Kirschen
 * @implNote The logic has been taken and transformed from https://www.javainuse.com/spring/boot-jwt.
 * @since 1.0.0
 */
class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private final TokenProperties tokenProperties;
    private final TokenParserService tokenParserService;
    private final TokenCacheService tokenCacheService;

    @Autowired
    JwtAuthorizationFilter(
        AuthenticationManager authenticationManager, TokenProperties tokenProperties, TokenParserService tokenParserService,
        TokenCacheService tokenCacheService
    ) {
        super(authenticationManager);
        this.tokenProperties = tokenProperties;
        this.tokenParserService = tokenParserService;
        this.tokenCacheService = tokenCacheService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(tokenProperties.getHeader());
        if (!StringUtils.isEmpty(token) && token.startsWith(tokenProperties.getPrefix())) {
            try {
                Jws<Claims> parsedToken = tokenParserService.parseToken(token);

                String username = parsedToken.getBody().getSubject();
                List<GrantedAuthority> authorities = ((List<?>) parsedToken.getBody().get("rol")).stream().map(
                    authority -> new SimpleGrantedAuthority((String) authority)).collect(Collectors.toList());

                if (!StringUtils.isEmpty(username)) {
                    return new UsernamePasswordAuthenticationToken(username, null, authorities);
                }
            } catch (ExpiredJwtException e) {
                LOGGER.warn("Request to parse expired JWT : {} failed : {}", token, e.getMessage());
                tokenCacheService.removeToken(token);
            } catch (UnsupportedJwtException e) {
                LOGGER.warn("Request to parse unsupported JWT : {} failed : {}", token, e.getMessage());
                tokenCacheService.removeToken(token);
            } catch (MalformedJwtException e) {
                LOGGER.warn("Request to parse invalid JWT : {} failed : {}", token, e.getMessage());
                tokenCacheService.removeToken(token);
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Request to parse empty or null JWT : {} failed : {}", token, e.getMessage());
                tokenCacheService.removeToken(token);
            } catch (SignatureException e) {
                LOGGER.warn("Request to parse not locally computed JWT : {} failed : {}", token, e.getMessage());
                tokenCacheService.removeToken(token);
            }
        }
        return null;
    }
}

