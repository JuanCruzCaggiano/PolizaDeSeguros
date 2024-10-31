package com.alkemy.bbva.polizaDeSeguros.dtos;

import com.alkemy.bbva.polizaDeSeguros.enums.EstadoCliente;
import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import lombok.*;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String direccion;
    private String telefono;
    private String tipoDocumento;
    private String nroDocumento;
    private Date fechaNacimiento;
    private String email;
    private EstadoCliente estado;

    public ClienteDTO mapFromCliente(final Cliente cliente) {
        return ClienteDTO.builder()
                .id(cliente.getIdCliente())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .tipoDocumento(cliente.getTipoDocumento())
                .nroDocumento(cliente.getNroDocumento())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .email(cliente.getEmail())
                .estado(cliente.getEstado())
                .build();
    }

}
