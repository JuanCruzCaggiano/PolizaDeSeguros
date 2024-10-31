package com.alkemy.bbva.polizaDeSeguros.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="TipoSeguro")
public class TipoSeguro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTipoSeguro;

    private String descripcion;

    @Column(name="prima-base")
    private double primaBase;

    public TipoSeguro(String descripcion, double primaBase) {
        this.descripcion = descripcion;
        this.primaBase = primaBase;
    }
}
