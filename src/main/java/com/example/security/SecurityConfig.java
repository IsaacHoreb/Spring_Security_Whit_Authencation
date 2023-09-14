package com.example.security;

import com.example.security.filters.JwtAuthenticationFilter;
import com.example.security.filters.JwtAuthorizationFilter;
import com.example.security.jwt.JwtUtils;
import com.example.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//@EnableGlobalMethodSecurity(prePostEnabled = true) <----Agrego ya creado a case de TestRolesController, esto quiere decir que habilitamos las anotaciones paraa nuestro controladores
//Añado despues en el TestRolesController las anotaciones a los enpoint @PreAuthorize

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    //1.Ya creado el 2do filtro, inyectamos en esta clase(-!-)
    @Autowired
    JwtUtils jwtUtils;

    //2.-(-!-)
    @Autowired
    JwtAuthorizationFilter authorizationFilter;

    //#Inyeccion (#)
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager) throws Exception { //FILTRO PARA AUTHENTICAR

        //Descpues de crear los flitro y la inyeccion hecha en (#), quitamos el .httpBasic
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager); //Agregamos el atributo
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return httpSecurity
                .csrf(config -> config.disable()) //Desabilito ya que no trabajare con formulario
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/hello").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter) //#Poner #esto, nos #autenticaramos #correctamente
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class) //Agrego esto parametr ya creado e inyectado el 2do filtro, que le dicimo que se va a ejecutar ante que el UsernamePasswordAuthenticationFilter
                .build();

        //despues de añadir .addFilterBefore y funcionar correctamente, creamos en controller la clase TestRolesController
        //.httpBasic()//#Esta_autenticacion lo #hacemos con un USER en #memoria[UserDetailsService] y .add()
    }


 /* Comento ya que tengo que autenticar con los USUARIO que tengo en la DB
    @Bean
    UserDetailsService userDetailsService() { //Para crear un USER en memoria, para que funcione crearemos --AuthenticationManager--
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        manager.createUser(User
                .withUsername("isaac")
                .password("1234")
                .roles()
                .build());

        return manager;
    }
    --Crearemos nuestra clase personalizada, para eso creamos la carpeta SERVICE
    */


    @Bean
    PasswordEncoder passwordEncoder() {
        //return NoOpPasswordEncoder.getInstance(); //NoOpPasswordEncoder -> para no #encritar la #contraseña
        return new BCryptPasswordEncoder(); //#Algoritmo de #incriptacion de Password, #debemos ir a PrincipalController, y #anadirlo ya para #encriptarlo al #enviarlo a la BD
    }

    //AuthenticationManager se #encargara de #administrar la #authenticacion
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)//#Agrego la #inyeccio que cree (#) ya que #cambio por lo que #tenia de USER en #memoria
                .passwordEncoder(passwordEncoder)
                .and().build();
    }

    /*
    public static void main(String[] args) {
        System.out.println(new BCryptPasswordEncoder().encode("1234"));
    }
    */


}
