package com.github.yannickkirschen.spring.bearer;

import com.github.yannickkirschen.spring.bearer.token.TokenCacheService;
import com.github.yannickkirschen.spring.bearer.token.TokenProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The {@link JwtAuthenticationFilter} manages the authentication of a user. It checks the credentials and creates a token if they're correct.
 *
 * @author Yannick Kirschen
 * @implNote The logic has been taken and transformed from https://www.javainuse.com/spring/boot-jwt.
 * @since 1.0.0
 */
class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final TokenProperties tokenProperties;
    private final AuthenticationManager authenticationManager;
    private final TokenCacheService tokenCacheService;

    @Autowired
    JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenProperties tokenProperties, TokenCacheService tokenCacheService) {
        this.authenticationManager = authenticationManager;
        this.tokenProperties = tokenProperties;
        this.tokenCacheService = tokenCacheService;
        setFilterProcessesUrl(tokenProperties.getAuthenticationUrl());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getHeader("username");
        String password = request.getHeader("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, Authentication authentication)
        throws IOException {
        User user = ((User) authentication.getPrincipal());
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        byte[] signingKey = tokenProperties.getSecret().getBytes();
        String username = user.getUsername();
        String token = Jwts
            .builder()
            .signWith(Keys.hmacShaKeyFor(signingKey), SignatureAlgorithm.HS512)
            .setHeaderParam("typ", tokenProperties.getType())
            .setIssuer(tokenProperties.getIssuer())
            .setAudience(tokenProperties.getAudience())
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + 600_000))
            .claim("rol", roles)
            .compact();

        LOGGER.info("Logged in user '{}'.", username);
        tokenCacheService.addToken(token, username);

        response.addHeader("Content-Type", "application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("{" + "   \"token\": \"" + token + "\"" + "}");
        response.getWriter().flush();
        response.getWriter().close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed)
        throws IOException, ServletException {
        LOGGER.warn("Unsuccessful authentication.");
        super.unsuccessfulAuthentication(request, response, failed);
    }
}

