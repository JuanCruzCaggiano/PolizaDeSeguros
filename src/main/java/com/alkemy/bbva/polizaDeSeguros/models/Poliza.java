package com.alkemy.bbva.polizaDeSeguros.models;

import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="Poliza")
public class Poliza {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPoliza;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "id_tipo_seguro", nullable = false)
    private TipoSeguro tipoSeguro;

    @NotNull(message = "La descripción de la póliza no debe ser nula")
    @Size(min=3 , max=100, message = "La descripción de la póliza debe " +
            "   tener al menos 3 caracteres y no superar los 100")
    private String descripcion;

    @Min(value=1000, message = "El importe de monto asegurado debe ser mayor que cero")
    @Column(name="monto-asegurado")
    private double montoAsegurado;

    @Past(message = "La fecha de emision debe ser anterior a la actual")
    @Column(name="fecha-emision")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaEmision;

    @Past(message = "La fecha de vencimiento debe ser anterior a la actual")
    @Column(name="fecha-vencimiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date fechaVencimiento;

    @Enumerated(EnumType.STRING)
    private EstadoPoliza estado;
}
