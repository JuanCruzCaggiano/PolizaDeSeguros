package com.alkemy.bbva.polizaDeSeguros.config;

import com.alkemy.bbva.polizaDeSeguros.enums.EstadoCliente;
import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import com.alkemy.bbva.polizaDeSeguros.models.Usuario;
import com.alkemy.bbva.polizaDeSeguros.repositories.ClienteRepository;
import com.alkemy.bbva.polizaDeSeguros.repositories.TipoSeguroRepository;
import com.alkemy.bbva.polizaDeSeguros.repositories.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(TipoSeguroRepository tipoSeguroRepository,
                                      ClienteRepository clienteRepository,
                                      UsuarioRepository usuarioRepository) {
        return args -> {
            // Tipos de seguros predefinidos
            if (tipoSeguroRepository.count() == 0) {
                tipoSeguroRepository.save(new TipoSeguro("Auto", 100D));
                tipoSeguroRepository.save(new TipoSeguro("Inmueble", 500D));
                tipoSeguroRepository.save(new TipoSeguro("Celular", 800D));
            }

            // Clientes predefinidos
            if (clienteRepository.count() == 0) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                clienteRepository.save(new Cliente("Juan", "Pérez",
                        "Liniers", "123456789", "DNI",
                        "40399768", sdf.parse("1990-01-01"),
                        "juancruz.caggiano@bbva.com", EstadoCliente.ACTIVO));
                clienteRepository.save(new Cliente("María", "López",
                        "Burzaco", "134563", "DNI",
                        "41253253", sdf.parse("1990-02-01"),
                        "juancruz.caggiano@bbva.com", EstadoCliente.ACTIVO));
                clienteRepository.save(new Cliente("Carlos", "García",
                        "Mataderos", "678943", "DNI",
                        "42876549", sdf.parse("1990-03-01"),
                        "juancruz.caggiano@bbva.com", EstadoCliente.ACTIVO));
            }

            // Usuarios predefinidos
            if (usuarioRepository.count() == 0) {
                usuarioRepository.save(
                        new Usuario(
                                "juancruz.caggiano@bbva.com",
                                "$2a$10$UGaZsxous81YRRP3aDIyQu1P/lrVd0nKxxqIcz9LQAxHalz3V9eWa",
                                Boolean.TRUE,
                                "ADMIN"));
            }
        };
    }
}
