package com.project.demo.logic.entity.email;

import com.project.demo.logic.entity.notification.Notification;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private SendGrid sendGrid;

    @Value("${spring.sendgrid.sender.email}")
    private String senderEmail;

    public void sendEmail(String toEmail, String subject, String body) {
        Email from = new Email(senderEmail, "Tributico");
        Email to = new Email(toEmail);
        Content content = new Content("text/html", body);
        Mail mail = new Mail(from,subject,to,content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() >= 400) {
                System.err.println("Error al enviar email: " + response.getBody());
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void sendNotificationEmail(String toEmail, String name, Notification notification) {
        String subject = notification.getName();

        String htmlContent = String.format(
                "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #FFF1C1;'>"
                        + "<div style='max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); overflow: hidden;'>"

                        + "  <div style='background-color: #4A403D; color: #FFF1C1; padding: 20px; text-align: center;'>"
                        + "    <h1 style='margin: 0; font-size: 24px;'>TribuTico</h1>"
                        + "    <p style='margin: 0; font-size: 14px;'>Notificación del sistema</p>"
                        + "  </div>"

                        + "  <div style='padding: 25px;'>"
                        + "    <h2 style='color: #4A403D; font-size: 20px;'>%s</h2>"
                        + "    <p style='font-size: 16px; color: #4A403D; line-height: 1.6;'>Estimado/a %s,</p>"
                        + "    <p style='font-size: 16px; color: #4A403D; line-height: 1.6;'>%s</p>"
                        + "    <p style='margin-top: 20px; font-size: 14px; color: #A69A90;'>📅 Fecha límite: <strong>%s</strong></p>"
                        + "    <p style='margin-top: 30px; font-size: 14px; color: #4A403D;'>"
                        + "      Si ya realizaste este trámite, puedes ignorar este mensaje. De lo contrario, te recomendamos tomar acción antes de la fecha indicada."
                        + "    </p>"
                        + "    <p style='margin-top: 20px; font-size: 14px; color: #4A403D;'>"
                        + "      Atentamente,<br><strong>El equipo de TribuTico</strong>"
                        + "    </p>"
                        + "  </div>"

                        + "  <div style='background-color: #FACF7D; padding: 15px; text-align: center; color: #4A403D; font-size: 12px;'>"
                        + "    Este mensaje fue generado automáticamente por TribuTico. Por favor, no respondas a este correo."
                        + "  </div>"

                        + "</div>"
                        + "</div>",
                escapeHtml(notification.getName()),
                escapeHtml(name),
                escapeHtml(notification.getDescription()),
                notification.getCloseDate()
        );


        sendEmail(toEmail, subject, htmlContent);
    }

    private String escapeHtml(String input) {
        return input == null ? "" : input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

}
