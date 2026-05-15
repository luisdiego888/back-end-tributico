package com.project.demo.logic.entity.llm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LLMService {
    @Value("${spring.ai.ollama.chat.options.model}")
    private String model;

    @Autowired
    private ChatModel chatModel;

    public String generateInvoiceJson(String invoice, String type) {
        String prompt = String.format("""
                        From the OCR text between [[[ ]]] of a Costa Rican electronic invoice of type %s, complete ONLY the fields of the following JSON. Do not add explanations, headers, or comments. Return exclusively a valid and well-formed JSON (without any additional text). Use `null` if data is unavailable or illegible.
                        
                        If the invoice type is `"ingreso"`, include only the `receiver` (client) information.
                        If the invoice type is `"gasto"`, include only the `issuer` (supplier) information.
                        Completely omit the other field.
                        
                        Automatically classify each item in the `details` field into the `"category"` key using one of the following **category codes** according to the text content:
                        
                        VG-B   = Venta Gravada de Bienes (13%%)
                        VG-S   = Venta Gravada de Servicios (13%%)
                        VE     = Venta Exenta (Ley u oficio)
                        VX     = Venta Exonerada (usuario final exonerado)
                        EXP    = Exportación de Bienes o Servicios  
                        CBG    = Compra de Bienes Gravados (con derecho a crédito fiscal)  
                        CSG    = Compra de Servicios Gravados (con derecho a crédito fiscal)  
                        CX     = Compra Exenta o Exonerada (sin derecho a crédito)  
                        CBR    = Compra de Bienes con Tarifa Reducida (1%%, 2%%)  
                        CSR    = Compra de Servicios con Tarifa Reducida (1%%, 2%%)  
                        GSP    = Gasto de Servicios Públicos - Gravado  
                        GA     = Gasto Administrativo - Gravado  
                        SPS    = Servicios Profesionales Subcontratados - Gravado  
                        HP     = Honorarios Profesionales - Gravado  
                        GV     = Gasto de Transporte o Viáticos - Gravado  
                        PP     = Publicidad y Promoción - Gravado  
                        ALO    = Alquiler de Local u Oficina  
                        MR     = Mantenimiento y Reparaciones - Gravado  
                        CAF    = Compra de Activos Fijos - IVA prorrateable  
                        GF     = Gasto Financiero - No lleva IVA  
                        GS     = Gasto Salarial - No lleva IVA  
                        NCE    = Notas de Crédito Emitidas  
                        NCR    = Notas de Crédito Recibidas  
                        DON    = Donación Deducible - No lleva IVA  
                        MUL    = Multas, Sanciones o Gastos No Deducibles
                        
                        Requirements:
                        - All required fields must be present according to the invoice type.
                        - The JSON must be syntactically correct.
                        - Use correct data types.
                        - Do not include fields outside the schema.
                        - Do not include explanations, headers, or additional text.
                        - Do not touch the `type` field; it fills itself.
                        
                        Required format:
                        {
                          "consecutive": <string>,
                          "key": <string>,
                          "issueDate": "<yyyy-mm-dd>",
                        
                          "issuer": {
                            "name": "<string>",
                            "lastname": "<string>",
                            "identification": <int>,
                            "email": "<string>"
                          },
                          "receiver": {
                            "name": "<string>",
                            "lastname": "<string>",
                            "identification": <int>,
                            "email": "<string>"
                          },
                        
                          "details": [
                            {
                              "cabys": <string(13 digits)>,
                              "quantity": <float>,
                              "unit": "<string>",
                              "unitPrice": <float>,
                              "discount": <float>,
                              "tax": <float>,  // Only allowed values: %s, %s, %s, %s, %s, or %s,
                              "taxAmount": <float>,
                              "category": "<string (category code)>",
                              "total": <float>,
                              "description": "<string>",
                            }
                          ]
                        }
                        
                        OCR Text:
                        [[[
                        %s
                        ]]]
                        """, type, TaxRateEnum.TAX_RATE_GENERAL.getRate(), TaxRateEnum.TAX_RATE_REDUCED.getRate(),
                TaxRateEnum.TAX_RATE_SPECIAL.getRate(), TaxRateEnum.TAX_RATE_ADJUSTED.getRate(),
                TaxRateEnum.TAX_RATE_MINIMUM.getRate(), TaxRateEnum.TAX_RATE_ZERO.getRate(), invoice);


        OllamaOptions options = OllamaOptions.builder()
                .model(model)
                .build();

        ChatResponse response = chatModel.call(new Prompt(prompt, options));
        return response.getResult().getOutput().getText();
    }

    public String cleanJson(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("La respuesta del modelo está vacía.");
        }
        int start = input.indexOf('{');
        int end = input.lastIndexOf('}');
        if (start != -1 && end != -1 && start < end) {
            String json = input.substring(start, end + 1).trim();

            try {
                new ObjectMapper().readTree(json);
                return json;
            } catch (IOException e) {
                throw new IllegalArgumentException("El texto extraído no es un JSON válido: " + e.getMessage());
            }
        }
        throw new IllegalArgumentException("No se encontró un JSON válido en la respuesta.");
    }

    public String generateTaxRecommendations(String declaration, String objective, String targetDate, String userContext) {
        String prompt = String.format("""
    Genera una lista simulada de acciones hipotéticas que un usuario del ATV de Hacienda podría considerar
para alcanzar el siguiente objetivo en un escenario educativo y no vinculante:

OBJETIVO: %s
TIPO: %s
FECHA: %s

Instrucciones:
- Genera exactamente 3 ideas cortas (máximo 1 línea cada una)
- No uses lenguaje imperativo, describe opciones de forma neutral
- Enfócate solo en aspectos administrativos y de organización de datos
- Si es IVA: ejemplos sobre organización de facturas, control de compras/ventas, registro de fechas
- Si es RENTA/ISR: ejemplos sobre clasificación de gastos, registros contables, seguimiento de ingresos
- No uses expresiones como "usted debe", "recomendamos" o "asesoría"
- Presenta la respuesta así:
1. [idea]
2. [idea]
3. [idea]
""", objective, declaration, targetDate);

        OllamaOptions options = OllamaOptions.builder()
                .model(model)
                .temperature(0.7)
                .topP(0.9)
                .build();

        ChatResponse response = chatModel.call(new Prompt(prompt, options));
        return response.getResult().getOutput().getText().trim();

    }
}
