package com.github.yannickkirschen.spring.bearer.interfaces;

/**
 * The {@link JwtUser} is a user that can be used for jwt authorization. It has a username and a password.
 *
 * @author Yannick Kirschen
 * @since 1.0.0
 */
public interface JwtUser {
    /**
     * @return The username.
     */
    String getUsername();

    /**
     * @return The password.
     */
    String getPassword();
}
