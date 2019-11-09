package com.github.yannickkirschen.spring.bearer.token;

import com.github.yannickkirschen.common.caching.Cache;
import com.github.yannickkirschen.common.caching.impl.CacheFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The {@link TokenCacheService} caches the tokens for a user.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@Service
public class TokenCacheService {
    private final Cache<String, String> tokenCache;

    @Autowired
    public TokenCacheService(TokenProperties tokenProperties) {
        tokenCache = CacheFactory.getCache(tokenProperties.getCache());
    }

    /**
     * Adds a token to the cache.
     *
     * @param token    The token to add.
     * @param username The username the token belongs to.
     */
    public void addToken(String token, String username) {
        tokenCache.add(token, username);
    }

    /**
     * Finds a username for a token in the cache.
     *
     * @param token The token to get the username for.
     *
     * @return The username for the specified token. <code>null</code>, if the token does not exist.
     */
    String getUsernameForToken(String token) {
        return tokenCache.get(token);
    }

    /**
     * Removes a token and its username from the cache.
     *
     * @param token The token to remove.
     */
    public void removeToken(String token) {
        tokenCache.delete(token);
    }
}
