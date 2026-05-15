package com.project.demo.rest.invoice;

import com.project.demo.logic.entity.invoice.*;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.invoice.InvoiceService;
import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceRestController {
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceService invoiceService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Invoice> invoice;
        
        if (search.trim().isEmpty()) {
            if(userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SUPER_ADMIN"))) {
                invoice = invoiceRepository.findAll(pageable);
            } else {
                invoice = invoiceRepository.findByUserId(userPrincipal.getId(), pageable);
            }
        } else {
            invoice = invoiceRepository.searchInvoices(search.trim(), userPrincipal.getId(), pageable);
        }

        meta.setTotalPages(invoice.getTotalPages());
        meta.setTotalElements(invoice.getTotalElements());
        meta.setPageNumber(invoice.getNumber() + 1);
        meta.setPageSize(invoice.getSize());

        return new GlobalResponseHandler().handleResponse(
                "Facturas recuperadas exitosamente",
                invoice.getContent(),
                HttpStatus.OK,
                meta);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> createInvoice(@RequestBody Invoice invoice,
                                           @AuthenticationPrincipal User userPrincipal,
                                           HttpServletRequest request) {
        try {
            Invoice savedInvoice = invoiceService.saveInvoice(invoice, userPrincipal.getId());
            return new GlobalResponseHandler().handleResponse("Factura creada exitosamente",
                    savedInvoice, HttpStatus.CREATED, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al crear factura: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateInvoice(@PathVariable Long id,
                                           @RequestBody Invoice invoice,
                                           @AuthenticationPrincipal User userPrincipal,
                                           HttpServletRequest request) {
        try {
            invoice.setId(id);
            Invoice updatedInvoice = invoiceService.saveInvoice(invoice, userPrincipal.getId());
            return new GlobalResponseHandler().handleResponse("Factura actualizada correctamente",
                    updatedInvoice, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al actualizar factura: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> deleteInvoice(@PathVariable Long id, HttpServletRequest request) {
        try {
            invoiceService.deleteInvoice(id);
            return new GlobalResponseHandler().handleResponse("Factura eliminada exitosamente", id, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al eliminar factura: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getInvoiceById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Invoice invoice = invoiceService.getInvoiceById(id);
            return new GlobalResponseHandler().handleResponse("Factura encontrada", invoice, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Factura no encontrada", null, HttpStatus.NOT_FOUND, request);
        }
    }
}
