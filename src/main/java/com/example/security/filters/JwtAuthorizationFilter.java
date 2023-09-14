package com.example.security.filters;

import com.example.security.jwt.JwtUtils;
import com.example.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//Podemos ingresar el @Component ya que no enviarmeos atributos adiccionales
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter { //#Entendemos, el OncePerRequestFilter nos pedira siempre ingresar el TOKEN de acceso por enpoit

    //Despues de extender, agregamos el metodo por defecto que pide

    //Hacer esto, debemos validar el TOKEN
    @Autowired
    private JwtUtils jwtUtils;

    //Esto necesitaremos consultar el USER en la BD
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    //Agregamos los @NonNull ya que no puede enviarle NUll
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        //1.Extraer el token de la peticion
        String tokenHeader = request.getHeader("Authorization");

        //Validamos que sea diferente de null
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7); //estraemos el Header del token, debemos quitarlo

            if (jwtUtils.isTokenValid(token)) {
                String username = jwtUtils.getUsernameFromToken(token); //Aqui ya tenemos el USER

                //Necesitamos recuperar los detalles del USER
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                //Despues de recuperar, nos vamos ha autenticar
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());

                //Este contiene la autenticacion propia de la APP
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response); //Por si no entraste en el IF, te seguira negando el acceso
    }

    //Ya creado este filtro, ir a SecurityConfig e inyectamos(-!-)


}
