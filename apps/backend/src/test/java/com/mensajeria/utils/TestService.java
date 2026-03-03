package com.mensajeria.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class TestService {

    @PersistenceContext
    private EntityManager entityManager;

    public void removeAllUsers() {
        entityManager.createNativeQuery("DELETE FROM authorities").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM users").executeUpdate();
        // no, lo de abajo no es un error, está bien
        entityManager.createNativeQuery("ALTER SEQUENCE public.users_id_seq RESTART WITH 1").executeUpdate();

    }


}