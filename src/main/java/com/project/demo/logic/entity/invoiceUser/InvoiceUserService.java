package com.project.demo.logic.entity.invoiceUser;

import com.project.demo.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceUserService {

    @Autowired
    private InvoiceUserRepository invoiceUserRepository;

    public InvoiceUser findOrCreateFromInvoiceUser(InvoiceUser userFromRequest) {
        InvoiceUser existingUser = invoiceUserRepository.findByIdentification(userFromRequest.getIdentification());
        if (existingUser != null) {
            existingUser.setName(userFromRequest.getName());
            existingUser.setLastName(userFromRequest.getLastName());
            existingUser.setEmail(userFromRequest.getEmail());
            return invoiceUserRepository.save(existingUser);
        }
        return invoiceUserRepository.save(userFromRequest);
    }

    public InvoiceUser findOrCreateFromUser(User user) {
        InvoiceUser existingUser = invoiceUserRepository.findByIdentification(user.getIdentification());
        if (existingUser != null) {
            existingUser.setName(user.getName());
            existingUser.setLastName(user.getLastname());
            existingUser.setEmail(user.getEmail());
            return invoiceUserRepository.save(existingUser);
        }
        InvoiceUser newUser = new InvoiceUser();
        newUser.setName(user.getName());
        newUser.setLastName(user.getLastname());
        newUser.setEmail(user.getEmail());
        newUser.setIdentification(user.getIdentification());
        return invoiceUserRepository.save(newUser);
    }
}
