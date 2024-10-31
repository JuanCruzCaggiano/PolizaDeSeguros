package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.dtos.CrearUsuarioDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.UsuarioDTO;
import com.alkemy.bbva.polizaDeSeguros.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<Void> crear(@RequestBody CrearUsuarioDTO crearUsuarioDTO) {
        usuarioService.crear(crearUsuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
