package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.dtos.ClienteDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.PolizaDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.TipoSeguroDTO;
import com.alkemy.bbva.polizaDeSeguros.enums.EstadoCliente;
import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.alkemy.bbva.polizaDeSeguros.exceptions.ResourceNotFoundException;
import com.alkemy.bbva.polizaDeSeguros.services.ClienteService;
import com.alkemy.bbva.polizaDeSeguros.services.EmailService;
import com.alkemy.bbva.polizaDeSeguros.services.PolizaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class PolizaControllerTest {

    @InjectMocks
    private PolizaController polizaController;

    @Mock
    private PolizaService polizaService;

    @Mock
    private EmailService emailService;

    @Mock
    private ClienteService clienteService;

    private PolizaDTO polizaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNombre("Juan");
        clienteDTO.setApellido("Pérez");
        clienteDTO.setDireccion("Calle Falsa 123");
        clienteDTO.setTelefono("123456789");
        clienteDTO.setTipoDocumento("DNI");
        clienteDTO.setNroDocumento("12345678");
        clienteDTO.setFechaNacimiento(new Date());
        clienteDTO.setEmail("juancruz.caggiano@bbva.com");
        clienteDTO.setEstado(EstadoCliente.ACTIVO);

        TipoSeguroDTO tipoSeguroDTO = new TipoSeguroDTO();
        tipoSeguroDTO.setId(1L);
        tipoSeguroDTO.setDescripcion("Seguro de Vida");
        tipoSeguroDTO.setPrimaBase(500.0);

        polizaDTO = new PolizaDTO();
        polizaDTO.setId(1L);
        polizaDTO.setDescripcion("Poliza de prueba");
        polizaDTO.setMontoAsegurado(200000.0);
        polizaDTO.setFechaEmision(new Date());
        polizaDTO.setFechaVencimiento(new Date());
        polizaDTO.setEstado(EstadoPoliza.VIGENTE);
        polizaDTO.setIdCliente(clienteDTO.getId());
        polizaDTO.setIdTipoSeguro(tipoSeguroDTO.getId());
    }

    @Test
    void crearPoliza_deberiaCrearPolizaYEnviarEmail() {
        ClienteDTO clienteSimulado = new ClienteDTO();
        clienteSimulado.setId(1L);
        clienteSimulado.setEmail("juancruz.caggiano@bbva.com");

        when(clienteService.buscarClientePorId(polizaDTO.getIdCliente())).thenReturn(Optional.of(clienteSimulado));
        when(polizaService.crearPoliza(any(PolizaDTO.class))).thenReturn(polizaDTO);

        var response = polizaController.crearPoliza(polizaDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(polizaDTO, response.getBody());

        verify(emailService, times(1)).sendEmail(
                eq(clienteSimulado.getEmail()),
                eq("Nueva Póliza Creada"),
                eq("Estimado cliente, su póliza ha sido creada exitosamente. Con id " + polizaDTO.getId())
        );
    }

    @Test
    void obtenerPolizas_deberiaRetornarListaDePolizas() {
        when(polizaService.obtenerPolizas()).thenReturn(Collections.singletonList(polizaDTO));

        var response = polizaController.obtenerPolizas();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void obtenerPolizaPorId_deberiaLanzarExceptionSiNoSeEncuentraPoliza() {
        when(polizaService.buscarPolizaPorId(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> polizaController.obtenerPolizaPorId(1L));
    }

    @Test
    void actualizarPoliza_deberiaActualizarPolizaYEnviarEmail() {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setNombre("Juan");
        clienteDTO.setApellido("Pérez");
        clienteDTO.setDireccion("Calle Falsa 123");
        clienteDTO.setTelefono("123456789");
        clienteDTO.setTipoDocumento("DNI");
        clienteDTO.setNroDocumento("12345678");
        clienteDTO.setFechaNacimiento(new Date());
        clienteDTO.setEmail("juancruz.caggiano@bbva.com");
        clienteDTO.setEstado(EstadoCliente.ACTIVO);

        TipoSeguroDTO tipoSeguroDTO = new TipoSeguroDTO();
        tipoSeguroDTO.setId(1L);
        tipoSeguroDTO.setDescripcion("Seguro de Vida");
        tipoSeguroDTO.setPrimaBase(500.0);


        PolizaDTO polizaActualizada = new PolizaDTO();
        polizaActualizada.setId(1L);
        polizaActualizada.setDescripcion("Poliza Actualizada");
        polizaActualizada.setMontoAsegurado(250000.0);
        polizaActualizada.setEstado(EstadoPoliza.VIGENTE);
        polizaActualizada.setFechaEmision(new Date());
        polizaActualizada.setFechaVencimiento(new Date());
        polizaActualizada.setIdCliente(clienteDTO.getId());
        polizaActualizada.setIdTipoSeguro(tipoSeguroDTO.getId());

        when(polizaService.actualizarPoliza(eq(1L), any(PolizaDTO.class))).thenReturn(polizaActualizada);

        when(clienteService.buscarClientePorId(1L)).thenReturn(Optional.of(clienteDTO));

        var response = polizaController.actualizarPoliza(1L, polizaActualizada);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(polizaActualizada, response.getBody());

        verify(emailService, times(1)).sendEmail(
                eq(clienteDTO.getEmail()),
                eq("Póliza actualizada correctamente"),
                eq("Estimado cliente, su póliza ha sido actualizada exitosamente. Con id " + polizaActualizada.getId())
        );
    }

    @Test
    void eliminarPoliza_deberiaEliminarPolizaYRetornarNoContent() {
        doNothing().when(polizaService).eliminarPoliza(1L);

        var response = polizaController.eliminarPoliza(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void buscarVigentes_deberiaRetornarListaDePolizasVigentes() {
        PolizaDTO polizaVigente = new PolizaDTO();
        polizaVigente.setId(1L);
        polizaVigente.setDescripcion("Poliza Vigente");
        polizaVigente.setEstado(EstadoPoliza.VIGENTE);

        when(polizaService.buscarPorEstado(EstadoPoliza.VIGENTE)).thenReturn(Collections.singletonList(polizaVigente));

        var response = polizaController.buscarVigentes();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(polizaVigente, response.getBody().get(0));
    }
}