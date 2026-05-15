package com.project.demo.logic.entity.invoice;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.demo.logic.entity.invoiceUser.InvoiceUser;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;

@Entity
@Table(name = "invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String consecutive;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private String type;

    @JsonProperty("key")
    private String invoiceKey;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "issuer_id")
    private InvoiceUser issuer;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private InvoiceUser receiver;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DetailsInvoice> details;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConsecutive() {
        return consecutive;
    }

    public String getInvoiceKey() {
        return invoiceKey;
    }

    public void setInvoiceKey(String invoiceKey) {
        this.invoiceKey = invoiceKey;
    }

    public void setConsecutive(String consecutive) {
        this.consecutive = consecutive;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public InvoiceUser getIssuer() {
        return issuer;
    }

    public void setIssuer(InvoiceUser issuer) {
        this.issuer = issuer;
    }

    public InvoiceUser getReceiver() {
        return receiver;
    }

    public void setReceiver(InvoiceUser receiver) {
        this.receiver = receiver;
    }

    public List<DetailsInvoice> getDetails() {
        return details;
    }

    public void setDetails(List<DetailsInvoice> details) {
        this.details = details;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;

    }
}
