package com.project.demo.logic.entity.rol;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Order(2)
@Component
public class UserSeeder implements ApplicationListener<ContextRefreshedEvent> {
        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        public UserSeeder(
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
                this.roleRepository = roleRepository;
                this.userRepository = userRepository;
                this.passwordEncoder = passwordEncoder;
        }

        @Override
        public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
                this.createUser("Jeffry", "Valverde", "Fallas", "super.admin@gmail.com", "superadmin123",
                                RoleEnum.SUPER_ADMIN, "305490740", LocalDate.of(1990, 1, 1));
                this.createUser("Miguel", "Perez", "Chacon", "test@gmail.com", "test123", RoleEnum.USER, "205490740",
                                LocalDate.of(1995, 1, 1));
        }

        private void createUser(String name, String lastName, String lastname2, String email, String password,
                        RoleEnum role, String identification, LocalDate birthDate) {
                User superAdminRole = new User();
                superAdminRole.setName(name);
                superAdminRole.setLastname(lastName);
                superAdminRole.setLastname2(lastname2);
                superAdminRole.setEmail(email);
                superAdminRole.setPassword(password);
                superAdminRole.setIdentification(identification);
                superAdminRole.setBirthDate(birthDate);
                superAdminRole.setStatus("active");

                Optional<Role> optionalRole = roleRepository.findByName(role);
                Optional<User> optionalUser = userRepository.findByEmail(superAdminRole.getEmail());

                if (optionalRole.isEmpty() || optionalUser.isPresent()) {
                        return;
                }

                var user = new User();
                user.setName(superAdminRole.getName());
                user.setLastname(superAdminRole.getLastname());
                user.setLastname2(superAdminRole.getLastname2());
                user.setEmail(superAdminRole.getEmail());
                user.setPassword(passwordEncoder.encode(superAdminRole.getPassword()));
                user.setRole(optionalRole.get());
                user.setIdentification(superAdminRole.getIdentification());
                user.setBirthDate(superAdminRole.getBirthDate());
                user.setStatus("active");

                userRepository.save(user);
        }
}
