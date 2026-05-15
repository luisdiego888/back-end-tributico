package com.project.demo.rest.simulationExport;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Controller
public abstract class SimulationExportController<T, ID> {

    protected abstract Optional<T> findById(ID id);
    protected abstract String generatePdfContent(T simulation);
    protected abstract String generateCsvContent(T simulation);
    protected abstract String getPdfFileName();
    protected abstract String getCsvFileName();

    @GetMapping("/generate-pdf/{id}")
    public final ResponseEntity<byte[]> generatePdf(@PathVariable ID id) {
        Optional<T> optSimulation = findById(id);
        if (optSimulation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        T simulation = optSimulation.get();

        try {
            String htmlContent = generatePdfContent(simulation);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            ConverterProperties properties = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlContent, pdfDoc, properties);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.attachment().filename(getPdfFileName()).build());

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/generate-csv/{id}")
    public final ResponseEntity<byte[]> generateCsv(@PathVariable ID id) {
        Optional<T> optSimulation = findById(id);
        if (optSimulation.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        T simulation = optSimulation.get();
        String csvContent = generateCsvContent(simulation);
        byte[] csvBytes = csvContent.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(ContentDisposition.attachment().filename(getCsvFileName()).build());

        return new ResponseEntity<>(csvBytes, headers, HttpStatus.OK);
    }

}

