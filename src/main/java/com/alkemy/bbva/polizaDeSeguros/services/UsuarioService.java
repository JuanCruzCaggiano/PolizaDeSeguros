package com.alkemy.bbva.polizaDeSeguros.services;


import com.alkemy.bbva.polizaDeSeguros.dtos.CrearUsuarioDTO;
import com.alkemy.bbva.polizaDeSeguros.dtos.UsuarioDTO;
import com.alkemy.bbva.polizaDeSeguros.models.Usuario;
import com.alkemy.bbva.polizaDeSeguros.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    public Usuario getByUsername(final String username) {
        return usuarioRepository.findByUsername(username);
    }

    public List<UsuarioDTO> listar() {
        List<Usuario> list = usuarioRepository.findAll();
        return list.stream().map(usuario ->
                new UsuarioDTO().fromUsuario(usuario)
        ).toList();
    }

    public void crear(final CrearUsuarioDTO crearUsuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setUsername(crearUsuarioDTO.getUsername());
        usuario.setActivo(Boolean.TRUE);
        usuario.setPassword(passwordEncoder.encode(crearUsuarioDTO.getPassword()));
        usuario.setRoles(crearUsuarioDTO.getRoles());
        usuarioRepository.save(usuario);
    }

}