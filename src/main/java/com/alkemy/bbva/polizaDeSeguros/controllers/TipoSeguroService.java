package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.dtos.TipoSeguroDTO;
import com.alkemy.bbva.polizaDeSeguros.models.TipoSeguro;
import com.alkemy.bbva.polizaDeSeguros.repositories.TipoSeguroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TipoSeguroService {

    @Autowired
    private TipoSeguroRepository tipoSeguroRepository;

    public Optional<TipoSeguroDTO> buscarTipoSeguroPorId(Long id) {
        return tipoSeguroRepository.findById(id).map(tipoSeguro -> new TipoSeguroDTO().mapFromTipoSeguro(tipoSeguro));
    }

    public List<TipoSeguroDTO> obtenerTiposSeguros() {
        List<TipoSeguro> list = tipoSeguroRepository.findAll();
        return list.stream().map(tipoSeguro ->
                new TipoSeguroDTO().mapFromTipoSeguro(tipoSeguro)).collect(Collectors.toList());
    }
}
