package com.alkemy.bbva.polizaDeSeguros.controllers;

import com.alkemy.bbva.polizaDeSeguros.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /*@PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestHeader final String username,
            @RequestHeader final String password
    ) {
        return ResponseEntity.ok(authService.login(username, password));
    }*/

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        return ResponseEntity.ok(authService.login(username, password));
    }

}
