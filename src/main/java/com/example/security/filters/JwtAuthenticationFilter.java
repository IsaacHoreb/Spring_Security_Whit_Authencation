package com.example.security.filters;

import com.example.models.UserEntity;
import com.example.security.jwt.JwtUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { //Creamos la extends al principio

    //#Debemos #sobreescribir #unos dos #metodos, lo #aremos con click #derecho, ir a generate Override Methods

    private JwtUtils jwtUtils;

    //#Este #metodo es #CUANDO #INTENTAN #AUTHENTICARSE EN LA APP
    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {

        UserEntity userEntity = null; //#Aqui #tengo el User y Password
        String username = "";
        String password = "";

        //#Necesito #obtener el user y password, pero #debo #convertir el [request] que esta en JSON y #debemos #convertirlo al #OBJETO, hay que #mappearlo con ObjectMapper
        try {
            userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class); //#Hace el #proceso de #mappear el password y username
            username = userEntity.getUsername(); //#Acabo de #obtener el username
            password = userEntity.getPassword(); //#Acabo de #obtener el password
        } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //Si _todo #salio bien, nos #autethicamos
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return getAuthenticationManager().authenticate(authenticationToken);
    }

    //#Este #metodo es #CUANDO SE HA #AUTHETICADO #CORRECTAMENT
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //#Debemos #obtener los #detalles del USER si_todo #salio bien para la #authenticacion

        //Usar el USER de Spring Security
        User user = (User) authResult.getPrincipal();  //Debemos hacer un cat -> (User)
        String token = jwtUtils.generateAccesToken(user.getUsername()); //Con esto generamos el token de acceso para dar authorizacion a los otros einpot

        response.addHeader("Authorization", token);

        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("Message", "Autenticacion Correcta");
        httpResponse.put("Username", user.getUsername());

        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().flush(); //Me aseguro que _todo se escriba correctamente

        super.successfulAuthentication(request, response, chain, authResult);

        //Despues tengo que ir SecurityConfig, ya que tengo que cambiar el USER creado en memoria

    }
}
