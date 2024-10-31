package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.dtos.ClienteDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.PolizaDTO;
import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.alkemy.bbva.polizaDeSeguros.services.ClienteService;
import com.alkemy.bbva.polizaDeSeguros.services.EmailService;
import com.alkemy.bbva.polizaDeSeguros.services.PolizaService;
import com.alkemy.bbva.polizaDeSeguros.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/polizas")
public class PolizaController {

    @Autowired
    private PolizaService polizaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/crearPoliza")
    public ResponseEntity<PolizaDTO> crearPoliza(@RequestBody PolizaDTO polizaDTO) {
        PolizaDTO polizaCreada = polizaService.crearPoliza(polizaDTO);

        ClienteDTO clienteDePoliza = clienteService.buscarClientePorId(polizaDTO.getIdCliente())
                        .orElseThrow(()->new ResourceNotFoundException("Cliente no encontrado."));

        emailService.sendEmail(
                clienteDePoliza.getEmail(),
                "Nueva Póliza Creada",
                "Estimado cliente, su póliza ha sido creada exitosamente. Con id " + polizaCreada.getId()
        );
        return new ResponseEntity<>(polizaCreada, HttpStatus.CREATED);
    }

    @GetMapping("/obtenerPolizas")
    public ResponseEntity<List<PolizaDTO>> obtenerPolizas() {
        return ResponseEntity.ok(polizaService.obtenerPolizas());
    }

    @GetMapping("/{id}/obtenerPoliza")
    public ResponseEntity<PolizaDTO> obtenerPolizaPorId(@PathVariable Long id) {
        Optional<PolizaDTO> poliza = polizaService.buscarPolizaPorId(id);
        return poliza.map(ResponseEntity::ok).orElseThrow(() ->
                new ResourceNotFoundException("La póliza solicitada no ha sido encontrada."));
    }

    @PutMapping("/{id}/actualizarPoliza")
    public ResponseEntity<PolizaDTO> actualizarPoliza(@PathVariable Long id, @RequestBody PolizaDTO poliza) {
        PolizaDTO polizaActualizada = polizaService.actualizarPoliza(id, poliza);

        ClienteDTO clienteDePoliza = clienteService.buscarClientePorId(polizaActualizada.getIdCliente())
                .orElseThrow(()->new ResourceNotFoundException("Cliente no encontrado."));

        emailService.sendEmail(
                clienteDePoliza.getEmail(),
                "Póliza actualizada correctamente",
                "Estimado cliente, su póliza ha sido actualizada exitosamente. Con id " + polizaActualizada.getId()
        );

        return ResponseEntity.ok(polizaActualizada);
    }

    @DeleteMapping("/{id}/eliminarPoliza")
    public ResponseEntity<Void> eliminarPoliza(@PathVariable Long id) {
        polizaService.eliminarPoliza(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/obtenerPolizasVigentes")
    public ResponseEntity<List<PolizaDTO>> buscarVigentes() {
        return ResponseEntity.ok(polizaService.buscarPorEstado(EstadoPoliza.VIGENTE));
    }

    @GetMapping("/obtenerPolizasVencidas")
    public ResponseEntity<List<PolizaDTO>> buscarVencidos() {
        return ResponseEntity.ok(polizaService.buscarPorEstado(EstadoPoliza.VENCIDA));
    }

    @PutMapping("/{id}/actualizarEstadoPoliza")
    public ResponseEntity<String> actualizarEstadoPoliza(@PathVariable Long id) {
        boolean actualizado = polizaService.actualizarEstadoSiVencido(id);

        if (actualizado) {
            return new ResponseEntity<>("La póliza ha sido actualizada ya que ha vencido.", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("La póliza ya se ha vencido o no se encontró.", HttpStatus.OK);
        }
    }

    @GetMapping("/{monto}/montoMayor")
    public List<PolizaDTO> montoMayorQue(@PathVariable double monto) {
        return polizaService.polizaMontoMayorQue(monto);
    }
}