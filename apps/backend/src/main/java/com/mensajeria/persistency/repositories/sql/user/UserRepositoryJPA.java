package com.mensajeria.persistency.repositories.sql.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserRepositoryJPA {
    @Id
    private String username;  // PK

    private String password;
    private Boolean enabled;

    private Long id;  // no es PK
}
