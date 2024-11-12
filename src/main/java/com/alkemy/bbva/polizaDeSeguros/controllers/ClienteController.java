package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.dtos.ClienteDTO;
import com.alkemy.bbva.polizaDeSeguros.services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:5173")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping("/crearCliente")
    public ResponseEntity<ClienteDTO> crearCliente(@RequestBody ClienteDTO clienteDTO) {
        return new ResponseEntity<>(clienteService.crearCliente(clienteDTO), HttpStatus.CREATED);
    }

    @GetMapping("/obtenerClientes")
    public ResponseEntity<List<ClienteDTO>> obtenerClientes() {
        return ResponseEntity.ok(clienteService.obtenerClientes());
    }
}
