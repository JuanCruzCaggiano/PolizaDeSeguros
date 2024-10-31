package com.alkemy.bbva.polizaDeSeguros.repositories;

import com.alkemy.bbva.polizaDeSeguros.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Usuario findByUsername(String username);
}