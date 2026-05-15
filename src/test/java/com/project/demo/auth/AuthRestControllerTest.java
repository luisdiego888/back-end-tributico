package com.project.demo.auth;

import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.rest.auth.AuthRestController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthRestControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthRestController authRestController;

    private User testUser;
    private Role testRole;

    /**
     * - Creación de un rol de usuario de prueba
     * - Creación de un usuario de prueba
     * - Se utiliza ReflectionTestUtils para inyectar manualmente los mocks de los repositorios,
     *   permitiendo acceder e inicializar los campos privados para las pruebas.
     */
    @BeforeEach
    public void setUp() {

        testRole = new Role();
        testRole.setName(RoleEnum.USER);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("diego@gmail.com");
        testUser.setPassword("123");
        testUser.setRole(testRole);

        ReflectionTestUtils.setField(authRestController, "userRepository", userRepository);
        ReflectionTestUtils.setField(authRestController, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(authRestController, "roleRepository", roleRepository);

    }

    /**
     * Prueba unitaria para el método "authenticate" de la clase "AuthRestController",
     * encargada de verificar que retorne un token válido cuando un usuario se autentica correctamente
     *
     * La prueba unitaria realiza las siguientes acciones:
     * - Mockea la búsqueda del usuario por correo electrónico en el repositorio "userRepository"
     * - Mockea la autenticación del usuario en el servicio "authenticationService"
     * - Mockea la generacion del token y su tiempo de expiración
     * - Verifica que la respuesta HTTP sea 200 (OK)
     * - Verifica que el cuerpo de la respuesta no sea nulo y contenga el token esperado
     */
    @Test
    @DisplayName("Debe retornar un token válido cuando el usuario se autentica correctamente")
    public void authenticate_WithValidUser_ShouldReturnValidToken() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(authenticationService.authenticate(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("test-token");
        when(jwtService.getExpirationTime()).thenReturn(3600000L);

        ResponseEntity<LoginResponse> response = authRestController.authenticate(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test-token", response.getBody().getToken());
    }

    /**
     * Prueba unitaria para el método "registerUser" de la clase "AuthRestController",
     * encargada de verificar que retorne un estado HTTP 409 (Conflict) cuando el correo electrónico
     * del usuario ya está registrado
     *
     * La prueba unitaria realiza las siguientes acciones:
     * - Mockea la búsqueda de un usuario existente por correo electrónico
     * - Verifica que la respuesta HTTP sea 409 (Conflict)
     * - Verifica que no se llame al método "save" del repositorio para evitar registros duplicados
     */
    @Test
    @DisplayName("Debe retornar un estado de conflicto cuando el correo electrónico ya está registrado")
    void registerUser_WithExistingEmail_ShouldReturnConflict() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = authRestController.registerUser(testUser);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class));
    }

}
