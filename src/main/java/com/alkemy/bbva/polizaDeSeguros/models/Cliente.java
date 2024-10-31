package com.alkemy.bbva.polizaDeSeguros.models;

import com.alkemy.bbva.polizaDeSeguros.enums.EstadoCliente;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Cliente")
public class Cliente {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCliente;

    @NotNull(message = "El nombre del cliente no debe ser nulo")
    @Size(min=3 , max=100, message = "El nombre del cliente debe " +
            "   tener al menos 3 caracteres y no superar los 100")
    private String nombre;

    @NotNull(message = "El apellido del cliente no debe ser nulo")
    @Size(min=3 , max=100, message = "El apellido del cliente debe " +
            "   tener al menos 3 caracteres y no superar los 100")
    private String apellido;

    @NotNull(message = "La direccion del cliente no debe ser nula")
    private String direccion;

    @NotNull(message = "El telefono del cliente no debe ser nulo")
    private String telefono;

    @NotNull(message = "El tipo documento del cliente no debe ser nulo")
    @Column(name="tipo-documento")
    private String tipoDocumento;

    @NotNull(message = "El dni es obligatorio")
    @Pattern(regexp="\\d{7,8}", message = "El dni debe contener 7 u 8 digitos" )
    @Column(name="nro-documento")
    private String nroDocumento;

    @Past(message = "La fecha de nacimiento debe ser anterior a la actual")
    @Column(name="fecha-nacimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    @Email(message = "El mail debe tener formato valido")
    private String email;

    @Enumerated(EnumType.STRING)
    private EstadoCliente estado;

    public Cliente(String nombre, String apellido, String direccion, String telefono,
                   String tipoDocumento, String nroDocumento, Date fechaNacimiento, String email,
                   EstadoCliente estado) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.direccion = direccion;
        this.telefono = telefono;
        this.tipoDocumento = tipoDocumento;
        this.nroDocumento = nroDocumento;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.estado = estado;
    }
}
