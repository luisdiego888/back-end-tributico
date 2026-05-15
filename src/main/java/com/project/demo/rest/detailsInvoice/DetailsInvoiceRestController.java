package com.project.demo.rest.detailsInvoice;


import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.detailsInvoice.DetailsInvoiceRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import jakarta.servlet.http.HttpServletRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/details-invoice")
public class DetailsInvoiceRestController {
    @Autowired
    DetailsInvoiceRepository detailsInvoiceRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            HttpServletRequest request) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DetailsInvoice> detailsBillPage;

        if (search == null || search.trim().isEmpty()) {
            detailsBillPage = detailsInvoiceRepository.findAll(pageable);
        } else {
            detailsBillPage = detailsInvoiceRepository.searchBillsDetails(search.trim(), pageable);
        }

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(detailsBillPage.getTotalPages());
        meta.setTotalElements(detailsBillPage.getTotalElements());
        meta.setPageNumber(detailsBillPage.getNumber() + 1);
        meta.setPageSize(detailsBillPage.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Detalles recuperados exitosamente",
                detailsBillPage.getContent(),
                HttpStatus.OK,
                meta
        );
    }

    @PostMapping
    public ResponseEntity<?> createDetailsBill(@RequestBody DetailsInvoice detailsInvoice, HttpServletRequest request) {
        DetailsInvoice savedDetailsInvoice = detailsInvoiceRepository.save(detailsInvoice);
        return new GlobalResponseHandler().handleResponse("Detalles de facturas creados", savedDetailsInvoice, HttpStatus.CREATED, request);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getDetailsBillById(@PathVariable Long id, HttpServletRequest request) {
        Optional<DetailsInvoice> foundDetailsBill = detailsInvoiceRepository.findById(id);
        if (foundDetailsBill.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados", foundDetailsBill.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados" + id + "no fue encontrada", HttpStatus.NOT_FOUND, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateDetailsInvoice(@PathVariable Long id, @RequestBody DetailsInvoice detailsInvoice, HttpServletRequest request) {
        Optional<DetailsInvoice> foundDetailsInvoice = detailsInvoiceRepository.findById(id);
        if (foundDetailsInvoice.isPresent()) {
            detailsInvoice.setId(foundDetailsInvoice.get().getId());
            detailsInvoiceRepository.save(detailsInvoice);
            return new GlobalResponseHandler().handleResponse("Detalles actualizados exitosamente", detailsInvoice, HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados", HttpStatus.NOT_FOUND, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteDetailsBill(@PathVariable Long id, HttpServletRequest request) {
        Optional<DetailsInvoice> foundDetailsInvoice = detailsInvoiceRepository.findById(id);
        if (foundDetailsInvoice.isPresent()) {
            detailsInvoiceRepository.deleteById(id);
            return new GlobalResponseHandler().handleResponse("Detalles eliminados exitosamente", foundDetailsInvoice.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Detalles no encontrados", id, HttpStatus.NOT_FOUND, request);
        }
    }


}
