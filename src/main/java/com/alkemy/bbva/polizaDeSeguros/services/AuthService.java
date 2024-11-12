package com.alkemy.bbva.polizaDeSeguros.services;


import com.alkemy.bbva.polizaDeSeguros.config.JwtService;
import com.alkemy.bbva.polizaDeSeguros.exceptions.AlkemyException;
import com.alkemy.bbva.polizaDeSeguros.models.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsuarioService usuarioService;

    public Map<String, Object> login(final String username, final String password) {
        Map<String, Object> response = new HashMap<>();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            Usuario usuario = usuarioService.getByUsername(username);
            response.put("id", usuario.getId());
            response.put("token", jwtService.generateToken(usuario.getId(), username, usuario.getRoles()));
            response.put("nombre", usuario.getNombre());
            response.put("apellido", usuario.getApellido());
        } catch (Exception e) {
            throw new AlkemyException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas....");
        }
        return response;
    }

}