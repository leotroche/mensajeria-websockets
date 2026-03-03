package com.mensajeria.persistency.dao.jdbc;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class UserDAOJDBC {
    private final DataSource dataSource;
    private JdbcUserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    public UserDAOJDBC(DataSource dataSource, PasswordEncoder passwordEncoder) {
        this.dataSource = dataSource;
        this.userDetailsManager = new JdbcUserDetailsManager(dataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public void save(String username, String password) {
        UserDetails user = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("USER")
                .build();

        userDetailsManager.createUser(user);
    }


}
