package com.project.demo.logic.entity.xml;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoiceUser.InvoiceUser;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlService {

    public Invoice formatAndSave(InputStream inputStream, String type) throws Exception {
        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(inputStream);
            document.getDocumentElement().normalize();

            Invoice invoice = new Invoice();

            invoice.setType(type);
            invoice.setConsecutive(getText(document, "NumeroConsecutivo"));

            invoice.setInvoiceKey(getText(document, "Clave"));

            String issueDate = getText(document, "FechaEmision");
            if (issueDate != null && !issueDate.isEmpty()) {
                String datePart = issueDate.split("T")[0];
                invoice.setIssueDate(LocalDate.parse(datePart));
            }

            Element emisorElement = (Element) document.getElementsByTagName("Emisor").item(0);
            InvoiceUser issuer = new InvoiceUser();
            issuer.setName(getText(emisorElement, "Nombre"));
            issuer.setIdentification(getText(emisorElement, "Identificacion", "Numero"));
            issuer.setEmail(getText(emisorElement, "CorreoElectronico"));
            invoice.setIssuer(issuer);

            Element receptorElement = (Element) document.getElementsByTagName("Receptor").item(0);
            InvoiceUser receiver = new InvoiceUser();
            receiver.setName(getText(receptorElement, "Nombre"));
            receiver.setIdentification(getText(receptorElement, "Identificacion", "Numero"));
            receiver.setEmail(getText(receptorElement, "CorreoElectronico"));
            invoice.setReceiver(receiver);
            invoice.setInvoiceKey(getText(document, "CodigoActividad"));

            invoice.setDetails(extractDetails(document));

            return invoice;
        } catch (Exception e) {
            System.err.println("Error procesando XML: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private List<DetailsInvoice> extractDetails(Document document) {
        List<DetailsInvoice> detailList = new ArrayList<>();
        NodeList nodeList = document.getElementsByTagName("LineaDetalle");

        for (int i = 0; i < nodeList.getLength(); i++) {
            try {
                Element element = (Element) nodeList.item(i);
                DetailsInvoice detail = new DetailsInvoice();

                detail.setCabys(getText(element, "Codigo"));

                detail.setDescription(getText(element, "Detalle"));
                detail.setQuantity(Double.parseDouble(getText(element, "Cantidad")));
                detail.setUnitPrice(Double.parseDouble(getText(element, "PrecioUnitario")));

                detail.setUnit(getText(element, "UnidadMedida"));

                NodeList taxList = element.getElementsByTagName("Impuesto");
                if (taxList.getLength() > 0) {
                    Element taxElement = (Element) taxList.item(0);
                    detail.setTax(Double.parseDouble(getText(taxElement, "Tarifa")));
                    detail.setTaxAmount(Double.parseDouble(getText(taxElement, "Monto")));
                }

                detail.setTotal(Double.parseDouble(getText(element, "MontoTotalLinea")));

                detailList.add(detail);
            } catch (Exception e) {
                System.err.println("Error procesando línea " + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
        return detailList;
    }

    private String getText(Document doc, String tag) {
        NodeList list = doc.getElementsByTagName(tag);
        return (list.getLength() > 0) ? list.item(0).getTextContent() : null;
    }

    private String getText(Element parent, String childTag) {
        NodeList children = parent.getElementsByTagName(childTag);
        return (children.getLength() > 0) ? children.item(0).getTextContent().trim() : null;
    }

    private String getText(Element parent, String childTag, String grandchildTag) {
        NodeList children = parent.getElementsByTagName(childTag);
        if (children.getLength() > 0) {
            Element child = (Element) children.item(0);
            NodeList grandchildren = child.getElementsByTagName(grandchildTag);
            return (grandchildren.getLength() > 0) ? grandchildren.item(0).getTextContent().trim() : null;
        }
        return null;
    }

}