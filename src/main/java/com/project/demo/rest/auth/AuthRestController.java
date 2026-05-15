package com.project.demo.rest.auth;

import com.project.demo.logic.entity.auth.AuthenticationService;
import com.project.demo.logic.entity.auth.JwtService;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.LoginResponse;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@RequestMapping("/auth")
@RestController
public class AuthRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    public AuthRestController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody User user) {
        LoginResponse loginResponse = new LoginResponse();
        User authenticatedUser;

        try {
            authenticatedUser = authenticationService.authenticate(user);

            String jwtToken = jwtService.generateToken(authenticatedUser);
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());

            userRepository.findByEmail(user.getEmail())
                    .ifPresent(loginResponse::setAuthUser);

            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException ex) {
            loginResponse.setMessage("El nombre de usuario o la contraseña son incorrectos.");
            loginResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        } catch (Exception ex) {
            loginResponse.setMessage("Ocurrió un error interno. Inténtalo de nuevo más tarde.");
            loginResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginResponse);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El correo electrónico ya está en uso");
        }

        Optional<User> existingByIdentification = userRepository.findByIdentification(user.getIdentification());
        if (existingByIdentification.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("La cédula ya está en uso");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Rol no encontrado");
        }

        user.setRole(optionalRole.get());

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser);
    }


    @GetMapping("/google")
    public void googleAuth(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/me/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @PutMapping("/block")
    public ResponseEntity<?> blockUser(@RequestBody User user, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        if (foundUser.isPresent()) {
            user = foundUser.get();

            if (Objects.equals(user.getStatus(), "blocked")) {
                return new GlobalResponseHandler().handleResponse("El usuario ya está bloqueado", user, HttpStatus.OK,
                        request);
            }

            user.setStatus("blocked");
            userRepository.save(user);

            return new GlobalResponseHandler().handleResponse("Usuario bloqueado con éxito",
                    user, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Usuario " + user.getEmail() + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }

}