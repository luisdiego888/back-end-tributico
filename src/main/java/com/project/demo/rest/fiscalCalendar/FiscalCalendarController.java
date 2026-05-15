package com.project.demo.rest.fiscalCalendar;

import com.project.demo.logic.entity.fiscalCalendar.FiscalCalendarValidator;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.fiscalCalendar.FiscalCalendar;
import com.project.demo.logic.entity.fiscalCalendar.FiscalCalendarRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/fiscal-calendar")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class FiscalCalendarController {

    @Autowired
    private FiscalCalendarRepository fiscalCalendarRepository;

    @Autowired
    private FiscalCalendarValidator validator;

    @GetMapping
    public ResponseEntity<?> getAllFiscalEvents(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "5") int size,
                                                @RequestParam(defaultValue = "") String search,
                                                HttpServletRequest request) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<FiscalCalendar> fiscalPage;

            if (search == null || search.trim().isEmpty()) {
                fiscalPage = fiscalCalendarRepository.findAll(pageable);
            } else {
                fiscalPage = fiscalCalendarRepository.searchFiscalCalendar(search.trim(), pageable);
            }

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(fiscalPage.getTotalPages());
            meta.setTotalElements(fiscalPage.getTotalElements());
            meta.setPageNumber(fiscalPage.getNumber() + 1);
            meta.setPageSize(fiscalPage.getSize());

            return new GlobalResponseHandler().handleResponse(
                    "Fechas fiscales recuperadas exitosamente",
                    fiscalPage.getContent(),
                    HttpStatus.OK,
                    meta
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al buscar fechas fiscales", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PostMapping
    public ResponseEntity<?> createFiscalEvent(@RequestBody FiscalCalendar event, HttpServletRequest request) {
        try {
            if (event.getTaxDeclarationDeadline().isBefore(LocalDate.now().plusDays(3))) {
                return new GlobalResponseHandler().handleResponse("La fecha limite debe ser al menos 3 dias en el futuro", null, HttpStatus.BAD_REQUEST, request);
            }

            FiscalCalendar savedEvent = fiscalCalendarRepository.save(event);

            return new GlobalResponseHandler().handleResponse("Evento fiscal creado exitosamente", savedEvent, HttpStatus.CREATED, request);

        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al crear evento", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{Id}")
    public ResponseEntity<?> updateFiscalEvent(@PathVariable Long Id, @RequestBody FiscalCalendar event, HttpServletRequest request) {
        try {
            if (!validator.validateDeadline(event.getTaxDeclarationDeadline())) {
                return new GlobalResponseHandler().handleResponse("La fecha no puede ser anterior a la actual", null, HttpStatus.BAD_REQUEST, request);
            }

            Optional<FiscalCalendar> foundEvent = fiscalCalendarRepository.findById(Id);
            if (foundEvent.isEmpty()) {
                return new GlobalResponseHandler().handleResponse("No se encontro el evento", null, HttpStatus.NOT_FOUND, request);
            }

            FiscalCalendar updatedEvent = foundEvent.get();
            updatedEvent.setName(event.getName());
            updatedEvent.setDescription(event.getDescription());
            updatedEvent.setTaxDeclarationDeadline(event.getTaxDeclarationDeadline());
            updatedEvent.setType(event.getType());

            updatedEvent = fiscalCalendarRepository.save(updatedEvent);

            return new GlobalResponseHandler().handleResponse("Calendario actualizado exitosamente", updatedEvent, HttpStatus.OK, request);
        }catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al actualizar evento", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @DeleteMapping("/{fiscalId}")
    public ResponseEntity<?> deleteFiscalEvent(@PathVariable Long fiscalId, HttpServletRequest request) {
        try {
            Optional<FiscalCalendar> foundEvent = fiscalCalendarRepository.findById(fiscalId);

            foundEvent.ifPresent(fiscalCalendar -> fiscalCalendarRepository.delete(fiscalCalendar));

            return new GlobalResponseHandler().handleResponse("Calendario fiscal eliminado exitosamente", null, HttpStatus.OK, request);
        }catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al eliminar el calendario", null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }


}
