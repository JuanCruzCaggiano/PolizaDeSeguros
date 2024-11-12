package com.alkemy.bbva.polizaDeSeguros.dtos;

import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TipoSeguroDTO {
    private Long id;
    private String descripcion;
    private double primaBase;

    public TipoSeguroDTO mapFromTipoSeguro(final TipoSeguro tipoSeguro) {
        return TipoSeguroDTO.builder()
                .id(tipoSeguro.getIdTipoSeguro())
                .descripcion(tipoSeguro.getDescripcion())
                .primaBase(tipoSeguro.getPrimaBase())
                .build();
    }

    // Método toEntity() para convertir el DTO a la entidad TipoSeguro
    public TipoSeguro toEntity() {
        TipoSeguro tipoSeguro = new TipoSeguro();
        tipoSeguro.setIdTipoSeguro(this.id);
        tipoSeguro.setDescripcion(this.descripcion);
        tipoSeguro.setPrimaBase(this.primaBase);
        return tipoSeguro;
    }
}
