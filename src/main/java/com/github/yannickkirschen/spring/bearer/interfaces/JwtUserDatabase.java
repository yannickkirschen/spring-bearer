package com.github.yannickkirschen.spring.bearer.interfaces;

/**
 * The {@link JwtUserDatabase} allows accessing the database in order to get the user based on its name.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
@FunctionalInterface
public interface JwtUserDatabase {
    /**
     * Checks, if the user exists in the database and loads it.
     *
     * @param username The user to find.
     *
     * @return The user read from the database.
     *
     * @throws RuntimeException if the user does not exist.
     */
    JwtUser assertAndGet(String username);
}
