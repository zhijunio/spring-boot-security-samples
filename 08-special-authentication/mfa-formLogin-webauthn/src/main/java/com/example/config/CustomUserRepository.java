package com.example.config;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class CustomUserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String sqlUsers = "select * from users where username = ?";
    private final String sqlAurhorities = "select * from authorities where username = ?";

    public CustomUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public CustomUser findByUsername(String username) {
        List<CustomAuthorities> customAuthorities = jdbcTemplate.query(sqlAurhorities, new RowMapper<CustomAuthorities>() {
            @Override
            public CustomAuthorities mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomAuthorities(rs.getString("username"),
                        rs.getString("authority"));
            }
        }, username);
        List<String> roles = customAuthorities.stream().map(auth -> auth.authority()).toList();
        try {
            return jdbcTemplate.queryForObject(sqlUsers, new RowMapper<CustomUser>() {
                @Override
                public CustomUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new CustomUser(rs.getString("username"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getBoolean("enabled"),
                            roles);
                }
            }, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

}