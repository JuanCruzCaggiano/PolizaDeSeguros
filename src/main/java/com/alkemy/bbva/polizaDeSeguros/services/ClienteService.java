package com.alkemy.bbva.polizaDeSeguros.services;

import com.alkemy.bbva.polizaDeSeguros.dtos.ClienteDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.PolizaDTO;
import com.alkemy.bbva.polizaDeSeguros.models.Cliente;
import com.alkemy.bbva.polizaDeSeguros.repositories.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public ClienteDTO crearCliente(ClienteDTO clienteDTO) {
        Cliente cliente = new Cliente();
        cliente.setNombre(clienteDTO.getNombre());
        cliente.setApellido(clienteDTO.getApellido());
        cliente.setDireccion(clienteDTO.getDireccion());
        cliente.setTelefono(clienteDTO.getTelefono());
        cliente.setEstado(clienteDTO.getEstado());
        cliente.setTipoDocumento(clienteDTO.getTipoDocumento());
        cliente.setNroDocumento(clienteDTO.getNroDocumento());
        cliente.setFechaNacimiento(clienteDTO.getFechaNacimiento());
        cliente.setEmail(clienteDTO.getEmail());
        clienteRepository.save(cliente);
        return new ClienteDTO().mapFromCliente(cliente);
    }

    public Optional<ClienteDTO> buscarClientePorId(Long id) {
        return clienteRepository.findById(id).map(cliente -> new ClienteDTO().mapFromCliente(cliente));
    }

    public List<ClienteDTO> obtenerClientes() {
        List<Cliente> list = clienteRepository.findAll();
        return list.stream().map(cliente ->
                new ClienteDTO().mapFromCliente(cliente)).collect(Collectors.toList());
    }
}
