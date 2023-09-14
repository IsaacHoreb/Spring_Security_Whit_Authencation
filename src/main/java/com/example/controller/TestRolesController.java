/*
    La importancia de esta clase es para que mejorar la aministracion
     de los roles, queremos que solo los ADMIN, administren el sistema
*/
package com.example.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestRolesController {

    @GetMapping("/accessAdmin")
    @PreAuthorize("hasRole('ADMIN')") //Despues de añadir la anotacion en EnableGlobalMethodSecurity en la clase SecurityConfig
    public String accessAdmin() {
        return "Hola, has accedito con rol de ADMIN";
    }

    @GetMapping("/accessUser")
    @PreAuthorize("hasRole('USER')")
    public String accessUser() {
        return "Hola, has accedito con rol de USER";
    }

    @GetMapping("/accessInvited")
    @PreAuthorize("hasRole('INVITED')")
    public String accessInvited() {
        return "Hola, has accedito con rol de INVITED";
    }
}
