package com.example.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data //Para generar los Getter y Setter
@AllArgsConstructor // Constructor con todos los parametros
@NoArgsConstructor // Constructor sin paramentros
@Builder //Implementa el Patron de Diseño Builder para construir objeto de esta clase
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @NotBlank // No vacio
    @Size(max = 80) // Cantidad de caracteres
    private String email;

    @NotBlank
    @Size(max = 30)
    private String username;

    @NotBlank
    private String password;

    //#Creamos la #relacion entre UserEntity y RoleEntity
    // 1.-Como lo #traera, - Con #cual se #relaciona-, -No #eliminar mis roles-
    //2.-#Creamos la #Foraing Key

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class, cascade = CascadeType.PERSIST)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;
}
