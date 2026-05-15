package com.project.demo.rest.isrSimulation;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.isrSimulation.IsrRepository;
import com.project.demo.logic.entity.isrSimulation.IsrSimulation;
import com.project.demo.logic.entity.isrSimulation.TaxIsrCalculationService;
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
@RequestMapping("/isr-simulation")
public class IsrSimulationRestController {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaxIsrCalculationService taxIsrCalculationService;

    @Autowired
    private IsrRepository isrRepository;

    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createSimulation(
            @RequestParam(defaultValue = "2024") int year,
            @RequestParam(defaultValue = "0") int childrenNumber,
            @RequestParam(defaultValue = "false") boolean hasSpouse,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request
    ) {
        Optional<User> userOpt = userRepository.findById(userPrincipal.getId());
        if (userOpt.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Usuario no encontrado", null, HttpStatus.NOT_FOUND, request);
        }

        List<Invoice> invoices = invoiceRepository.findByYear(year, userPrincipal.getId());
        IsrSimulation sim = taxIsrCalculationService.simulate(userOpt.get(), invoices, year, childrenNumber, hasSpouse);

        return new GlobalResponseHandler().handleResponse("Simulación generada correctamente", sim, HttpStatus.OK, request);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> saveSimulation(@RequestBody IsrSimulation simulation,
                                            HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(simulation.getUser().getId());
        if (foundUser.isPresent()) {
            simulation.setUser(foundUser.get());
            IsrSimulation savedSimulation = isrRepository.save(simulation);
            return new GlobalResponseHandler().handleResponse("Simulación guardada exitosamente",
                    savedSimulation, HttpStatus.CREATED, request);

        } else {
            return new GlobalResponseHandler().handleResponse("No se encontro el usuario",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<IsrSimulation> simulationPage;

        if (search == null || search.trim().isEmpty()) {
            simulationPage = isrRepository.findByUserId(userPrincipal.getId(), pageable);
        } else {
            simulationPage = isrRepository.searchIsrSimulation(search.trim(), userPrincipal.getId(),
                    pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(simulationPage.getTotalPages());
        meta.setTotalElements(simulationPage.getTotalElements());
        meta.setPageNumber(simulationPage.getNumber() + 1);
        meta.setPageSize(simulationPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Simulaciones recuperadas exitosamente",
                simulationPage.getContent(),
                HttpStatus.OK,
                meta);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteIsrSimulation(@PathVariable Long id, HttpServletRequest request) {
        Optional<IsrSimulation> foundSimulation = isrRepository.findById(id);
        if (foundSimulation.isPresent()) {
            isrRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Simulación eliminada exitosamente",
                    foundSimulation.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Simulación no encontrada",
                    id, HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getSimulationByUserId(@PathVariable Long userId, HttpServletRequest request) {
        List<IsrSimulation> simulation = isrRepository.findByUserId(userId);
        if (simulation.isEmpty()) {
            return new GlobalResponseHandler().handleResponse("Usuario no tiene simulaciones registradas",
                    simulation, HttpStatus.NOT_FOUND, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Simulaciones recuperadas exitosamente",
                    simulation, HttpStatus.OK, request);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getSimulationById(@PathVariable Long id, HttpServletRequest request) {
        Optional<IsrSimulation> simulation = isrRepository.findById(id);
        if (simulation.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Simulación encontrada",
                    simulation,HttpStatus.OK, request);
        }

        return new GlobalResponseHandler().handleResponse("Simulación no encontrada",
                null,HttpStatus.NOT_FOUND, request);
    }
}
