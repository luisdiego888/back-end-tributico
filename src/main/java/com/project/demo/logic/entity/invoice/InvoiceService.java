package com.project.demo.logic.entity.invoice;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoiceService;
import com.project.demo.logic.entity.invoiceUser.InvoiceUserService;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InvoiceUserService invoiceUserService;
    @Autowired
    private DetailsInvoiceService detailsInvoiceService;

    public Invoice saveInvoice(Invoice invoice, Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + userId));

        Invoice invoiceToSave;
        if (invoice.getId() != null) {
            invoiceToSave = invoiceRepository.findById(invoice.getId())
                    .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + invoice.getId()));
            invoiceToSave.setConsecutive(invoice.getConsecutive());
            invoiceToSave.setInvoiceKey(invoice.getInvoiceKey());
            invoiceToSave.setIssueDate(invoice.getIssueDate());
            invoiceToSave.setType(invoice.getType());

            detailsInvoiceService.clearInvoiceDetails(invoiceToSave);
            invoiceToSave.getDetails().clear();
        } else {
            invoiceToSave = new Invoice();
            invoiceToSave.setUser(currentUser);
            invoiceToSave.setConsecutive(invoice.getConsecutive());
            invoiceToSave.setInvoiceKey(invoice.getInvoiceKey());
            invoiceToSave.setIssueDate(invoice.getIssueDate());
            invoiceToSave.setType(invoice.getType());
        }

        invoiceToSave.setIssuer(
                invoice.getIssuer() != null ? invoiceUserService.findOrCreateFromInvoiceUser(invoice.getIssuer()) : invoiceUserService.findOrCreateFromUser(currentUser)
        );

        invoiceToSave.setReceiver(
                invoice.getReceiver() != null ? invoiceUserService.findOrCreateFromInvoiceUser(invoice.getReceiver()) : invoiceUserService.findOrCreateFromUser(currentUser)
        );

        Invoice savedInvoice = invoiceRepository.save(invoiceToSave);

        if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
            savedInvoice.setDetails(detailsInvoiceService.processInvoiceDetails(invoice.getDetails(), savedInvoice));
        }

        return savedInvoice;
    }

    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
    }

    public void deleteInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con ID: " + id));
        invoiceRepository.delete(invoice);
    }
}