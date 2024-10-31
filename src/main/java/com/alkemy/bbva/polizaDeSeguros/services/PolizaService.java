package com.alkemy.bbva.polizaDeSeguros.services;

import com.alkemy.bbva.polizaDeSeguros.dtos.PolizaDTO;
import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import com.alkemy.bbva.polizaDeSeguros.models.Poliza;
import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import com.alkemy.bbva.polizaDeSeguros.repositories.ClienteRepository;
import com.alkemy.bbva.polizaDeSeguros.repositories.PolizaRepository;
import com.alkemy.bbva.polizaDeSeguros.exceptions.ResourceNotFoundException;
import com.alkemy.bbva.polizaDeSeguros.repositories.TipoSeguroRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolizaService {

    @Autowired
    private PolizaRepository polizaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TipoSeguroRepository tipoSeguroRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(PolizaService.class);

    public PolizaDTO crearPoliza(PolizaDTO polizaDTO) {
        logger.info("Creando nueva póliza...");

        Cliente cliente = clienteRepository.findById(polizaDTO.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        TipoSeguro tipoSeguro = tipoSeguroRepository.findById(polizaDTO.getIdTipoSeguro())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de Seguro no encontrado"));

        Poliza poliza = new Poliza();
        poliza.setDescripcion(polizaDTO.getDescripcion());
        poliza.setMontoAsegurado(polizaDTO.getMontoAsegurado());
        poliza.setFechaEmision(polizaDTO.getFechaEmision());
        poliza.setFechaVencimiento(polizaDTO.getFechaVencimiento());
        poliza.setEstado(polizaDTO.getEstado());
        poliza.setCliente(cliente);
        poliza.setTipoSeguro(tipoSeguro);
        polizaRepository.save(poliza);
        logger.info("Póliza creada con éxito");
        return new PolizaDTO().mapFromPoliza(poliza);
    }

    public List<PolizaDTO> obtenerPolizas() {
        List<Poliza> list = polizaRepository.findAll();
        return list.stream().map(poliza ->
                new PolizaDTO().mapFromPoliza(poliza)).collect(Collectors.toList());
    }

    public Optional<PolizaDTO> buscarPolizaPorId(Long id) {
        return polizaRepository.findById(id).map(poliza -> new PolizaDTO().mapFromPoliza(poliza));
    }

    public PolizaDTO actualizarPoliza(Long id, PolizaDTO polizaActualizada) {
        return polizaRepository.findById(id)
                .map(poliza -> {
                    poliza.setMontoAsegurado(polizaActualizada.getMontoAsegurado());
                    poliza.setFechaVencimiento(polizaActualizada.getFechaVencimiento());
                    poliza.setEstado(polizaActualizada.getEstado());
                    poliza.setDescripcion(polizaActualizada.getDescripcion());
                    Poliza polizaGuardada = polizaRepository.save(poliza);

                    // Convertir Poliza a PolizaDTO
                    PolizaDTO polizaDTO = new PolizaDTO();
                    polizaDTO.setId(polizaGuardada.getIdPoliza());
                    polizaDTO.setMontoAsegurado(polizaGuardada.getMontoAsegurado());
                    polizaDTO.setFechaEmision(polizaGuardada.getFechaEmision());
                    polizaDTO.setFechaVencimiento(polizaGuardada.getFechaVencimiento());
                    polizaDTO.setEstado(polizaGuardada.getEstado());
                    polizaDTO.setDescripcion(polizaGuardada.getDescripcion());
                    polizaDTO.setIdCliente(polizaGuardada.getCliente().getIdCliente());
                    polizaDTO.setIdTipoSeguro(polizaGuardada.getTipoSeguro().getIdTipoSeguro());

                    return polizaDTO;
                }).orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
    }

    public void eliminarPoliza(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        emailService.sendEmail(
                poliza.getCliente().getEmail(),
                "Póliza eliminada correctamente",
                "Estimado cliente, su póliza ha sido eliminada exitosamente. Con id " + id
        );

        polizaRepository.delete(poliza);
    }

    public List<PolizaDTO> buscarPorEstado(final EstadoPoliza estado) {
        List<Poliza> list = polizaRepository.findByEstado(estado);
        return list.stream().map(poliza ->
                new PolizaDTO().mapFromPoliza(poliza)).collect(Collectors.toList());
    }

    public boolean actualizarEstadoSiVencido(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        Date fechaActual = new Date();

        if (poliza.getFechaVencimiento().before(fechaActual) && poliza.getEstado() == EstadoPoliza.VIGENTE) {
            poliza.setEstado(EstadoPoliza.VENCIDA);
            polizaRepository.save(poliza);

            emailService.sendEmail(
                    poliza.getCliente().getEmail(),
                    "Póliza actualizada correctamente",
                    "Estimado cliente, su póliza ha vencido y se encuentra actualizada. Con id " + id
            );

            return true;
        }
        return false;
    }

    public List<PolizaDTO> polizaMontoMayorQue(final double monto) {
        List<Poliza> list = polizaRepository.findByMontoGreaterThan(monto);
        return list.stream().map(poliza ->
                new PolizaDTO().mapFromPoliza(poliza)).collect(Collectors.toList());
    }
}
