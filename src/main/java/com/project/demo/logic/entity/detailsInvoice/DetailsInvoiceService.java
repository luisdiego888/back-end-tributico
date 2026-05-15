package com.project.demo.logic.entity.detailsInvoice;

import com.project.demo.logic.entity.invoice.Invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DetailsInvoiceService {

    @Autowired
    private DetailsInvoiceRepository detailsInvoiceRepository;

    public void clearInvoiceDetails(Invoice invoice) {
        if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
            detailsInvoiceRepository.deleteAll(invoice.getDetails());
        }
    }

    public List<DetailsInvoice> processInvoiceDetails(List<DetailsInvoice> inputDetails, Invoice savedInvoice) {
        return inputDetails.stream()
                .map(detail -> buildDetailInvoice(detail, savedInvoice))
                .map(detailsInvoiceRepository::save)
                .collect(Collectors.toList());
    }

    private DetailsInvoice buildDetailInvoice(DetailsInvoice detail, Invoice invoice) {
        DetailsInvoice newDetail = new DetailsInvoice();
        newDetail.setCabys(detail.getCabys());
        newDetail.setDescription(detail.getDescription());
        newDetail.setQuantity(detail.getQuantity());
        newDetail.setUnitPrice(detail.getUnitPrice());
        newDetail.setDiscount(detail.getDiscount());
        newDetail.setUnit(detail.getUnit());
        newDetail.setTax(detail.getTax());
        newDetail.setTaxAmount(detail.getTaxAmount());
        newDetail.setTotal(detail.getTotal());
        newDetail.setCategory(detail.getCategory());
        newDetail.setInvoice(invoice);
        return newDetail;
    }
}
