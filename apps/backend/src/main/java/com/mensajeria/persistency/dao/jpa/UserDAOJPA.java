package com.mensajeria.persistency.dao.jpa;

import com.mensajeria.persistency.repositories.sql.user.UserRepositoryJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAOJPA extends JpaRepository<UserRepositoryJPA, String> {
}
