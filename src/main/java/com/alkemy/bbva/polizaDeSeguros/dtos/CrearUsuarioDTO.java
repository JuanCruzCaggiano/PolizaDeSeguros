package com.alkemy.bbva.polizaDeSeguros.dtos;

import lombok.Data;

@Data
public class CrearUsuarioDTO {

    private String username;
    private String password;
    private String roles;
}
