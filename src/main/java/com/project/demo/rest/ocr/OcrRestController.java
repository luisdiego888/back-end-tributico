package com.project.demo.rest.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.llm.LLMService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;

@RestController
@RequestMapping("/ocr")
public class OcrRestController {

    @Autowired
    LLMService llmService;

    @PostMapping
    public ResponseEntity<?> extractText(@RequestParam("file") MultipartFile file, @RequestParam("type") String type, HttpServletRequest request) {
        try {
            String text;
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setSortByPosition(true);
                text = stripper.getText(document);
            }

            String jsonResponse = llmService.generateInvoiceJson(text, type);
            String cleanJson = llmService.cleanJson(jsonResponse);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode parsedJson = mapper.readTree(cleanJson);

            ObjectNode resultWithType = mapper.createObjectNode();
            resultWithType.setAll((ObjectNode) parsedJson);
            resultWithType.put("type", type);

            return new GlobalResponseHandler().handleResponse("Texto extraído exitosamente", resultWithType,
                    HttpStatus.OK, request);
        } catch (IOException e) {
            return new GlobalResponseHandler().handleResponse("Error al procesar el archivo PDF", e.getMessage(),
                    HttpStatus.BAD_REQUEST, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error inesperado al generar el JSON", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request
            );
        }
    }
}