package com.github.yannickkirschen.spring.bearer;

import com.github.yannickkirschen.spring.bearer.token.TokenCacheService;
import com.github.yannickkirschen.spring.bearer.token.TokenParserService;
import com.github.yannickkirschen.spring.bearer.token.TokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * The {@link SecurityConfiguration} configures the security of the entire REST service. It manages that all endpoints under "/jwiki/server/public" are filtered
 * by the {@link JwtAuthorizationFilter}.
 *
 * @author Yannick Kirschen
 * @implNote The logic has been taken and transformed from https://www.javainuse.com/spring/boot-jwt.
 * @since 1.0.0
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@SuppressWarnings("unused")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final TokenProperties tokenProperties;
    private final TokenParserService tokenParserService;
    private final TokenCacheService tokenCacheService;
    private final JwtUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfiguration(
        TokenParserService tokenParserService, JwtUserDetailsService userDetailsService, TokenProperties tokenProperties, TokenCacheService tokenCacheService
    ) {
        this.tokenProperties = tokenProperties;
        this.tokenParserService = tokenParserService;
        this.userDetailsService = userDetailsService;
        this.tokenCacheService = tokenCacheService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
            .and()
            .csrf()
            .disable()
            .authorizeRequests()
            .antMatchers(tokenProperties.getPublicUrl())
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .addFilter(new JwtAuthenticationFilter(authenticationManager(), tokenProperties, tokenCacheService))
            .addFilter(new JwtAuthorizationFilter(authenticationManager(), tokenProperties, tokenParserService, tokenCacheService))
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}

