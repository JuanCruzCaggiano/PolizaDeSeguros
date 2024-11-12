package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.dtos.TipoSeguroDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/tipoSeguro")
@CrossOrigin(origins = "http://localhost:5173")
public class TipoSeguroController {

    @Autowired
    private TipoSeguroService tipoSeguroService;

    @GetMapping("/obtenerTiposSeguros")
    public ResponseEntity<List<TipoSeguroDTO>> obtenerTiposSeguros() {
        return ResponseEntity.ok(tipoSeguroService.obtenerTiposSeguros());
    }
}
