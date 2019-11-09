package com.github.yannickkirschen.spring.bearer;

import com.github.yannickkirschen.spring.bearer.interfaces.JwtUser;
import com.github.yannickkirschen.spring.bearer.interfaces.JwtUserDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

/**
 * The {@link JwtUserDetailsService} is a wrapper for accessing the Database and load the user data.
 *
 * @author Yannick Kirschen
 * @implNote The logic has been taken and transformed from https://www.javainuse.com/spring/boot-jwt.
 * @since 1.0.0
 */
@Service
@SuppressWarnings("unused")
public class JwtUserDetailsService implements UserDetailsService {
    private static JwtUserDatabase database;

    public static void setDatabase(Object clazz) {
        JwtUserDetailsService.database = (JwtUserDatabase) clazz;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        JwtUser user = database.assertAndGet(username);
        return new User(user.getUsername(), passwordEncoder().encode(user.getPassword()), new LinkedList<>());
    }

    @Bean
    private PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
