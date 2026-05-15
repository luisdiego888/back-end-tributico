package com.project.demo.logic.entity.auth;

import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.rol.RoleRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


@Component
public class CustomOAuth2SuccesHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.front-end.base-url}")
    private String frontendUrl;

    @Autowired
    public CustomOAuth2SuccesHandler(JwtService jwtService, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {


        CustomOAuth2User oauthUser = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauthUser.getEmail();
        String googleId = oauthUser.getAttributes().get("sub").toString().substring(0, 9);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(oauthUser.getName());
                    newUser.setLastname(oauthUser.getLastName());
                    newUser.setRole(roleRepository.findByName(RoleEnum.USER)
                            .orElseThrow(() -> new RuntimeException("Role USER not found")));
                    newUser.setPassword(passwordEncoder.encode("User123"));
                    newUser.setIdentification(googleId);
                    return userRepository.save(newUser);
                });

        String jwtToken = jwtService.generateToken(user);


        String redirectUrl = UriComponentsBuilder.fromUriString(frontendUrl)
                .queryParam("token", jwtToken)
                .queryParam("expires_in", jwtService.getExpirationTime())
                .queryParam("email", user.getEmail())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);

    }
}
