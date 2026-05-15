package com.project.demo.rest.xml;

import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.xml.XmlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RequestMapping("/xml")
@RestController
public class XmlRestController {

    @Autowired
    private XmlService xmlService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','USER')")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type, HttpServletRequest request) {
        try (InputStream inputStream = file.getInputStream()) {
            Invoice saved = xmlService.formatAndSave(inputStream, type);
            return new GlobalResponseHandler().handleResponse("Procesamiento exitoso", saved, HttpStatus.OK,request);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
}
