package com.mensajeria.utils;

import com.mensajeria.persistency.dao.jdbc.UserDAOJDBC;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
@Transactional
public class TestService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    DataSource dataSource;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void removeAllUsers() {
        entityManager.createNativeQuery("DELETE FROM authorities").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        // no, lo de abajo no es un error, está bien
        entityManager.createNativeQuery("ALTER SEQUENCE public.users_id_seq RESTART WITH 1").executeUpdate();

    }

    public void createTestUsers() {
        UserDetails user1 = User.withUsername("pepe")
                .password(passwordEncoder.encode("pepe1234"))
                .roles("TESTUSER")
                .build();
        UserDetails user2 = User.withUsername("pepa")
                .password(passwordEncoder.encode("1234pepe"))
                .roles("TESTUSER")
                .build();

        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        boolean userPepeAlreadyExists = userDetailsManager.userExists(user1.getUsername());
        if (userPepeAlreadyExists) return;
        userDetailsManager.createUser(user1);
        userDetailsManager.createUser(user2);
    }


}