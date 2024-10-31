package com.alkemy.bbva.polizaDeSeguros.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "roles")
    private String roles;

    public Usuario(String username, String password, Boolean activo, String roles) {
        this.username = username;
        this.password = password;
        this.activo = activo;
        this.roles = roles;
    }

}
