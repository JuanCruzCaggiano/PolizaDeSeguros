package com.alkemy.bbva.polizaDeSeguros.repositories;

import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoSeguroRepository extends JpaRepository<TipoSeguro, Long> {

}
