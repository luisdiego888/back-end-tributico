package com.project.demo.rest.goals;

import com.project.demo.logic.entity.goals.Goals;
import com.project.demo.logic.entity.goals.GoalsRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.llm.LLMService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/goals")
public class GoalsController {

    @Autowired
    private GoalsRepository goalsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LLMService llmService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Goals> goalsPage;

        if (search == null || search.trim().isEmpty()) {
            goalsPage = goalsRepository.findByUserId(userPrincipal.getId(), pageable);
        } else {
            goalsPage = goalsRepository.searchGoals(search.trim(), userPrincipal.getId(),
                    pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(goalsPage.getTotalPages());
        meta.setTotalElements(goalsPage.getTotalElements());
        meta.setPageNumber(goalsPage.getNumber() + 1);
        meta.setPageSize(goalsPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Metas recuperadas exitosamente",
                goalsPage.getContent(),
                HttpStatus.OK,
                meta);
    }

    @PostMapping()
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createGoal(@RequestBody Goals goalRequest, HttpServletRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(goalRequest.getUser().getId());
            if (userOpt.isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "Usuario no encontrado", null, HttpStatus.NOT_FOUND, request);
            }

            if (goalRequest.getDeclaration() == null || goalRequest.getObjective() == null || goalRequest.getDate() == null) {
                return new GlobalResponseHandler().handleResponse(
                        "Declaración, objetivo y fecha son requeridos", null, HttpStatus.BAD_REQUEST, request);
            }

            Goals goal = new Goals();
            goal.setUser(userOpt.get());
            goal.setDeclaration(goalRequest.getDeclaration());
            goal.setType(goalRequest.getType());
            goal.setObjective(goalRequest.getObjective());
            goal.setDate(goalRequest.getDate());
            goal.setStatus("PENDING");

            try {
                String userContext = String.format("Usuario: %s %s",
                        userOpt.get().getName(),
                        userOpt.get().getLastname());

                String recommendations = llmService.generateTaxRecommendations(
                        goalRequest.getDeclaration(),
                        goalRequest.getObjective(),
                        goalRequest.getDate().toString(),
                        userContext
                );

                goal.setRecommendations(recommendations);

            } catch (Exception e) {
                goal.setRecommendations("Recomendaciones no disponibles en este momento.");
                System.err.println("Error generando recomendaciones: " + e.getMessage());
            }

            Goals savedGoal = goalsRepository.save(goal);

            return new GlobalResponseHandler().handleResponse(
                    "Meta registrada exitosamente con recomendaciones personalizadas",
                    savedGoal, HttpStatus.CREATED, request);

        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al crear meta: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getUserGoals(@PathVariable Long userId, HttpServletRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "Usuario no encontrado", null, HttpStatus.NOT_FOUND, request);
            }

            List<Goals> goals = goalsRepository.findByUser(userOpt.get());
            return new GlobalResponseHandler().handleResponse(
                    "Metas obtenidas exitosamente", goals, HttpStatus.OK, request);

        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al obtener metas: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }


    @DeleteMapping("/{goalId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteGoal(@PathVariable Long goalId, HttpServletRequest request) {
        try {
            if (!goalsRepository.existsById(goalId)) {
                return new GlobalResponseHandler().handleResponse(
                        "Meta no encontrada", null, HttpStatus.NOT_FOUND, request);
            }

            goalsRepository.deleteById(goalId);
            return new GlobalResponseHandler().handleResponse(
                    "Meta eliminada exitosamente", null, HttpStatus.OK, request);

        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al eliminar meta: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }
}