package com.example.repositories;

import com.example.models.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long> {

    //Crear el metodo para buscar por el USERNAME, JPA ya reconoce este metodo
    Optional<UserEntity> findByUsername(String username);

    //?1 = Obten el 1er paramentro que tengas
    @Query("select u from UserEntity u where u.username = ?1")
    Optional<UserEntity> getName(String username); //Si el caso, deseamos crear un metodo personalizado, que JPA no reconoce
}
