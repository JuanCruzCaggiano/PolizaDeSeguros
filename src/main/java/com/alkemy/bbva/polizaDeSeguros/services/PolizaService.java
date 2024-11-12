package com.alkemy.bbva.polizaDeSeguros.services;

import com.alkemy.bbva.polizaDeSeguros.controllers.TipoSeguroService;
import com.alkemy.bbva.polizaDeSeguros.dtos.ClienteDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.PolizaDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.TipoSeguroDTO;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    private ClienteService clienteService;

    @Autowired
    private TipoSeguroService tipoSeguroService;

    @Autowired
    private TipoSeguroRepository tipoSeguroRepository;

    @Autowired
    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(PolizaService.class);

    @CacheEvict(value = "polizas", allEntries = true)
    public PolizaDTO crearPoliza(PolizaDTO polizaDTO) {
        logger.info("Creando nueva póliza...");
        Cliente cliente = clienteRepository.findById(polizaDTO.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        TipoSeguro tipoSeguro = tipoSeguroRepository.findById(polizaDTO.getIdTipoSeguro())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de Seguro no encontrado"));

        Poliza poliza = new Poliza();
        poliza.setCodigo(polizaDTO.getCodigo());
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

    @Cacheable(value = "polizas", unless = "#result.isEmpty()")
    public List<PolizaDTO> obtenerPolizas() {
        logger.info("Buscando pólizas...");
        List<Poliza> list = polizaRepository.findAll();
        return list.stream().map(poliza ->
                new PolizaDTO().mapFromPoliza(poliza)).collect(Collectors.toList());
    }

    @Cacheable(value = "poliza", key = "#id", unless = "#result == null")
    public Optional<PolizaDTO> buscarPolizaPorId(Long id) {
        logger.info("Buscando póliza...");
        return polizaRepository.findById(id).map(poliza -> new PolizaDTO().mapFromPoliza(poliza));
    }

    @CachePut(value = "poliza", key = "#id")
    @CacheEvict(value = "polizas", allEntries = true)
    public PolizaDTO actualizarPoliza(Long id, PolizaDTO polizaActualizada) {
        logger.info("Actualizando póliza...");

        ClienteDTO clienteDePoliza = clienteService.buscarClientePorId(polizaActualizada.getIdCliente())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado."));

        TipoSeguroDTO tipoSeguroDePoliza = tipoSeguroService.buscarTipoSeguroPorId(polizaActualizada.getIdTipoSeguro())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de seguro no encontrado."));

        return polizaRepository.findById(id)
                .map(poliza -> {
                    poliza.setCodigo(polizaActualizada.getCodigo());
                    poliza.setMontoAsegurado(polizaActualizada.getMontoAsegurado());
                    poliza.setFechaVencimiento(polizaActualizada.getFechaVencimiento());
                    poliza.setEstado(polizaActualizada.getEstado());
                    poliza.setDescripcion(polizaActualizada.getDescripcion());
                    poliza.setCliente(clienteDePoliza.toEntity()); // Asegúrate de tener un método `toEntity()` en el DTO si es necesario
                    poliza.setTipoSeguro(tipoSeguroDePoliza.toEntity()); // Igual para el tipo de seguro

                    // Guardar la póliza actualizada
                    Poliza polizaGuardada = polizaRepository.save(poliza);

                    // Convertir Poliza a PolizaDTO
                    PolizaDTO polizaDTO = new PolizaDTO();
                    polizaDTO.setCodigo(polizaGuardada.getCodigo());
                    polizaDTO.setMontoAsegurado(polizaGuardada.getMontoAsegurado());
                    polizaDTO.setFechaEmision(polizaGuardada.getFechaEmision());
                    polizaDTO.setFechaVencimiento(polizaGuardada.getFechaVencimiento());
                    polizaDTO.setEstado(polizaGuardada.getEstado());
                    polizaDTO.setDescripcion(polizaGuardada.getDescripcion());
                    polizaDTO.setIdCliente(polizaGuardada.getCliente().getIdCliente());
                    polizaDTO.setIdTipoSeguro(polizaGuardada.getTipoSeguro().getIdTipoSeguro());

                    logger.info("Póliza actualizada con éxito");
                    return polizaDTO;
                }).orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));
    }

    @CacheEvict(value = "polizas", allEntries = true)
    public void eliminarPoliza(Long id) {
        logger.info("Eliminando póliza...");
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        emailService.sendEmail(
                poliza.getCliente().getEmail(),
                "Póliza eliminada correctamente",
                "Estimado cliente, su póliza ha sido eliminada exitosamente. Con código " + poliza.getCodigo()
        );

        polizaRepository.delete(poliza);
        logger.info("Póliza eliminada con éxito");
    }

    @Cacheable(value = "polizas", unless = "#result.isEmpty()")
    public List<PolizaDTO> buscarPorEstado(final EstadoPoliza estado) {
        logger.info("Buscando póliza...");
        List<Poliza> list = polizaRepository.findByEstado(estado);
        return list.stream().map(poliza ->
                new PolizaDTO().mapFromPoliza(poliza)).collect(Collectors.toList());
    }

    @CachePut(value = "poliza", key = "#id")
    @CacheEvict(value = "polizas", allEntries = true)
    public boolean actualizarEstadoSiVencido(Long id) {
        logger.info("Actualizando póliza...");
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada"));

        LocalDate fechaActual = LocalDate.now();

        if (poliza.getFechaVencimiento().isBefore(fechaActual) && poliza.getEstado() == EstadoPoliza.VIGENTE) {
            poliza.setEstado(EstadoPoliza.VENCIDA);
            polizaRepository.save(poliza);

            emailService.sendEmail(
                    poliza.getCliente().getEmail(),
                    "Póliza actualizada correctamente",
                    "Estimado cliente, su póliza ha vencido y se encuentra actualizada. Con código " + poliza.getCodigo()
            );

            logger.info("Póliza actualizada con éxito");
            return true;
        }
        return false;
    }

    public List<PolizaDTO> polizaMontoMayorQue(final double monto) {
        logger.info("Buscando póliza...");
        List<Poliza> list = polizaRepository.findByMontoGreaterThan(monto);
        return list.stream().map(poliza ->
                new PolizaDTO().mapFromPoliza(poliza)).collect(Collectors.toList());
    }
}
