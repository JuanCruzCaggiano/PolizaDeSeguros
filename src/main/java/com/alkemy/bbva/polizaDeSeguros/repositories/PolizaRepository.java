package com.alkemy.bbva.polizaDeSeguros.repositories;

import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.alkemy.bbva.polizaDeSeguros.models.Poliza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolizaRepository extends JpaRepository<Poliza, Long> {

    @Query("SELECT poliza FROM Poliza poliza WHERE poliza.montoAsegurado > :monto")
    List<Poliza> findByMontoGreaterThan(@Param("monto") double monto);

    List<Poliza> findByEstado(EstadoPoliza estado);

}
