package services;

import com.alkemy.bbva.polizaDeSeguros.dtos.PolizaDTO;
import com.alkemy.bbva.polizaDeSeguros.enums.EstadoCliente;
import com.alkemy.bbva.polizaDeSeguros.enums.EstadoPoliza;
import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import com.alkemy.bbva.polizaDeSeguros.models.Poliza;
import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import com.alkemy.bbva.polizaDeSeguros.repositories.ClienteRepository;
import com.alkemy.bbva.polizaDeSeguros.repositories.PolizaRepository;
import com.alkemy.bbva.polizaDeSeguros.repositories.TipoSeguroRepository;
import com.alkemy.bbva.polizaDeSeguros.services.EmailService;
import com.alkemy.bbva.polizaDeSeguros.services.PolizaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolizaServiceTest {

    @InjectMocks
    private PolizaService polizaService;

    @Mock
    private PolizaRepository polizaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TipoSeguroRepository tipoSeguroRepository;

    @Mock
    private EmailService emailService;

    private Poliza poliza;
    private PolizaDTO polizaDTO;
    private Cliente cliente;
    private TipoSeguro tipoSeguro;

    @BeforeEach
    public void setUp() {
        cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setTelefono("123456789");
        cliente.setTipoDocumento("DNI");
        cliente.setNroDocumento("12345678");
        cliente.setFechaNacimiento(new Date());
        cliente.setEmail("juancruz.caggiano@bbva.com");
        cliente.setEstado(EstadoCliente.ACTIVO);

        tipoSeguro = new TipoSeguro();
        tipoSeguro.setIdTipoSeguro(1L);
        tipoSeguro.setDescripcion("Seguro de Vida");
        tipoSeguro.setPrimaBase(500.0);

        polizaDTO = new PolizaDTO();
        polizaDTO.setIdCliente(cliente.getIdCliente());
        polizaDTO.setIdTipoSeguro(tipoSeguro.getIdTipoSeguro());
        polizaDTO.setCodigo(100L);
        polizaDTO.setDescripcion("Póliza de prueba");
        polizaDTO.setMontoAsegurado(10000.0);
        polizaDTO.setFechaEmision(new Date());
        polizaDTO.setFechaVencimiento(new Date(System.currentTimeMillis() + 86400000)); // Vence en un día
        polizaDTO.setEstado(EstadoPoliza.VIGENTE);

        poliza = new Poliza();
        poliza.setCodigo(100L);
        poliza.setCliente(cliente);
        poliza.setTipoSeguro(tipoSeguro);
        poliza.setCodigo(polizaDTO.getCodigo());
        poliza.setDescripcion(polizaDTO.getDescripcion());
        poliza.setMontoAsegurado(polizaDTO.getMontoAsegurado());
        poliza.setFechaEmision(polizaDTO.getFechaEmision());
        poliza.setFechaVencimiento(polizaDTO.getFechaVencimiento());
        poliza.setEstado(polizaDTO.getEstado());
    }

    @Test
    void crearPolizaTest() {
        when(clienteRepository.findById(anyLong())).thenReturn(Optional.of(cliente));
        when(tipoSeguroRepository.findById(anyLong())).thenReturn(Optional.of(tipoSeguro));
        when(polizaRepository.save(any(Poliza.class))).thenReturn(poliza);

        PolizaDTO resultado = polizaService.crearPoliza(polizaDTO);

        assertNotNull(resultado);
        assertEquals(polizaDTO.getIdCliente(), resultado.getIdCliente());
        assertEquals(polizaDTO.getIdTipoSeguro(), resultado.getIdTipoSeguro());
        verify(polizaRepository, times(1)).save(any(Poliza.class));
    }

    @Test
    void obtenerPolizasTest() {
        List<Poliza> polizas = new ArrayList<>();
        polizas.add(poliza);
        when(polizaRepository.findAll()).thenReturn(polizas);

        List<PolizaDTO> resultado = polizaService.obtenerPolizas();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(polizaRepository, times(1)).findAll();
    }

    @Test
    void buscarPolizaPorIdTest() {
        when(polizaRepository.findById(anyLong())).thenReturn(Optional.of(poliza));

        Optional<PolizaDTO> resultado = polizaService.buscarPolizaPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getIdCliente());
        verify(polizaRepository, times(1)).findById(anyLong());
    }

    @Test
    void eliminarPolizaTest() {
        when(polizaRepository.findById(anyLong())).thenReturn(Optional.of(poliza));

        polizaService.eliminarPoliza(1L);

        verify(polizaRepository, times(1)).delete(poliza);
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void actualizarEstadoSiVencidoTest() {
        poliza.setFechaVencimiento(new Date(System.currentTimeMillis() - 1000)); // Vencida
        poliza.setEstado(EstadoPoliza.VIGENTE);
        when(polizaRepository.findById(anyLong())).thenReturn(Optional.of(poliza));
        when(polizaRepository.save(any(Poliza.class))).thenReturn(poliza);

        boolean resultado = polizaService.actualizarEstadoSiVencido(1L);

        assertTrue(resultado);
        assertEquals(EstadoPoliza.VENCIDA, poliza.getEstado());
        verify(polizaRepository, times(1)).save(poliza);
    }

    @Test
    void polizaMontoMayorQueTest() {
        List<Poliza> polizas = new ArrayList<>();
        polizas.add(poliza);
        when(polizaRepository.findByMontoGreaterThan(anyDouble())).thenReturn(polizas);

        List<PolizaDTO> resultado = polizaService.polizaMontoMayorQue(5000);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(polizaRepository, times(1)).findByMontoGreaterThan(anyDouble());
    }
}