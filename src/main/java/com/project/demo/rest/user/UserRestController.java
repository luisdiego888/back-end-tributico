package com.project.demo.rest.user;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserRestController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> usersPage;

        if (search == null || search.trim().isEmpty()) {
            usersPage = userRepository.findAll(pageable);
        } else {
            usersPage = userRepository.searchUsers(search.trim(), pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(usersPage.getTotalPages());
        meta.setTotalElements(usersPage.getTotalElements());
        meta.setPageNumber(usersPage.getNumber() + 1);
        meta.setPageSize(usersPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Usuarios recuperados exitosamente",
                usersPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> addUser(@RequestBody User user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new GlobalResponseHandler().handleResponse("Usuario creado con éxito",
                user, HttpStatus.OK, request);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(userId);

        if (foundUser.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Usuario " + userId + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }

        Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
        if (userByEmail.isPresent() && !userByEmail.get().getId().equals(userId)) {
            return new GlobalResponseHandler().handleResponse("El correo electrónico ya está en uso por otro usuario",
                    HttpStatus.CONFLICT, request);
        }

        Optional<User> userByCedula = userRepository.findByIdentification(user.getIdentification());
        if (userByCedula.isPresent() && !userByCedula.get().getId().equals(userId)) {
            return new GlobalResponseHandler().handleResponse("La cédula ya está en uso por otro usuario",
                    HttpStatus.CONFLICT, request);
        }

        User existingUser = foundUser.get();
        existingUser.setName(user.getName());
        existingUser.setLastname(user.getLastname());
        existingUser.setLastname2(user.getLastname2());
        existingUser.setEmail(user.getEmail());
        existingUser.setIdentification(user.getIdentification());
        existingUser.setStatus(user.getStatus());

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            existingUser.setPassword(existingUser.getPassword());
        } else {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setRole(existingUser.getRole());

        userRepository.save(existingUser);
        return new GlobalResponseHandler().handleResponse("Usuario actualizado con éxito",
                existingUser, HttpStatus.OK, request);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        Optional<User> foundOrder = userRepository.findById(userId);
        if(foundOrder.isPresent()) {
            userRepository.deleteById(userId);
            return new GlobalResponseHandler().handleResponse("Usuario eliminado exitosamente",
                    foundOrder.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Usuario " + userId + " no encontrado"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateUserPatch(@PathVariable Long userId, @RequestBody User user, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Usuario " + userId + " no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }

        User existingUser = foundUser.get();

        if (user.getEmail() != null) {
            Optional<User> userByEmail = userRepository.findByEmail(user.getEmail());
            if (userByEmail.isPresent() && !userByEmail.get().getId().equals(userId)) {
                return new GlobalResponseHandler().handleResponse("El correo electrónico ya está en uso por otro usuario",
                        HttpStatus.CONFLICT, request);
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getIdentification() != null) {
            Optional<User> userByIdentification = userRepository.findByIdentification(user.getIdentification());
            if (userByIdentification.isPresent() && !userByIdentification.get().getId().equals(userId)) {
                return new GlobalResponseHandler().handleResponse("La cédula ya está en uso por otro usuario",
                        HttpStatus.CONFLICT, request);
            }
            existingUser.setIdentification(user.getIdentification());
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getLastname() != null) {
            existingUser.setLastname(user.getLastname());
        }
        if (user.getLastname2() != null) {
            existingUser.setLastname2(user.getLastname2());
        }
        if (user.getBirthDate() != null) {
            existingUser.setBirthDate(user.getBirthDate());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        userRepository.save(existingUser);

        return new GlobalResponseHandler().handleResponse("Usuario actualizado con éxito",
                existingUser, HttpStatus.OK, request);
    }


    @PatchMapping("/change-password/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> passwords, @PathVariable Long id, HttpServletRequest request) {
        String currentPassword = passwords.get("currentPassword");
        String newPassword = passwords.get("newPassword");

        Optional<User> foundUser = userRepository.findById(id);
        if (foundUser.isPresent()) {
            User existingUser = foundUser.get();
            if (!passwordEncoder.matches(currentPassword, existingUser.getPassword())) {
                return new GlobalResponseHandler().handleResponse("Contraseña actual incorrecta",
                        HttpStatus.BAD_REQUEST, request);
            }
            existingUser.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(existingUser);

            return new GlobalResponseHandler().handleResponse("Contraseña actualizada exitosamente",
                    existingUser, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Usuario no encontrado",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}