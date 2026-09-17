package com.example.user;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final JdbcClient jdbc;

    public UserService(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return this.jdbc.sql("SELECT username, password FROM users WHERE username = ?")
                    .param(username)
                    .query((rs, rowNum) -> User.withUsername(rs.getString("username"))
                            .password(rs.getString("password"))
                            .authorities("USER")
                            .build())
                    .single();
        }
        catch (EmptyResultDataAccessException ex) {
            throw new UsernameNotFoundException(username, ex);
        }
    }

}
