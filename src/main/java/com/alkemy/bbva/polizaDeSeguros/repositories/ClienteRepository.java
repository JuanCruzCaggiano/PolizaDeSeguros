package com.alkemy.bbva.polizaDeSeguros.repositories;

import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}
