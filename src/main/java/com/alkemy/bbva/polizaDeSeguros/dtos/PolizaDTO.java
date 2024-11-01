package com.alkemy.bbva.polizaDeSeguros.dtos;

import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import com.alkemy.bbva.polizaDeSeguros.models.Poliza;
import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import lombok.*;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PolizaDTO {
    private Long codigo;
    private String descripcion;
    private double montoAsegurado;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private EstadoPoliza estado;
    private Long idTipoSeguro;
    private Long idCliente;

    private String nombreCliente;
    private String apellidoCliente;
    private String emailCliente;
    private String nroDocumentoCliente;
    private String estadoCliente;

    private String descripcionTipoSeguro;

    public PolizaDTO mapFromPoliza(final Poliza poliza) {
        return PolizaDTO.builder()
                .codigo(poliza.getCodigo())
                .descripcion(poliza.getDescripcion())
                .montoAsegurado(poliza.getMontoAsegurado())
                .fechaEmision(poliza.getFechaEmision())
                .fechaVencimiento(poliza.getFechaVencimiento())
                .estado(poliza.getEstado())
                .idTipoSeguro(poliza.getTipoSeguro().getIdTipoSeguro())
                .idCliente(poliza.getCliente().getIdCliente())
                .nombreCliente(poliza.getCliente().getNombre())
                .apellidoCliente(poliza.getCliente().getApellido())
                .emailCliente(poliza.getCliente().getEmail())
                .nroDocumentoCliente(poliza.getCliente().getNroDocumento())
                .estadoCliente(poliza.getCliente().getEstado().name())
                .descripcionTipoSeguro(poliza.getTipoSeguro().getDescripcion())
                .build();
    }

}
