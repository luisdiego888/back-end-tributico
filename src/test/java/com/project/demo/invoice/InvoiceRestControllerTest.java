package com.project.demo.invoice;

import com.project.demo.logic.entity.detailsInvoice.DetailsInvoice;
import com.project.demo.logic.entity.invoice.Invoice;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.invoice.InvoiceService;
import com.project.demo.logic.entity.invoiceUser.InvoiceUser;
import com.project.demo.logic.entity.rol.Role;
import com.project.demo.logic.entity.rol.RoleEnum;
import com.project.demo.logic.entity.user.User;
import com.project.demo.rest.invoice.InvoiceRestController;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InvoiceRestControllerTest {

    @Mock
    private InvoiceRepository invoiceRepository;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @InjectMocks
    private InvoiceRestController invoiceRestController;

    private Invoice testInvoice;
    private User testUser;
    private List<DetailsInvoice> testDetails;
    private InvoiceUser testInvoiceUser;
    private Role testRole;

    /**
     * - Creación de una factura de prueba
     * - Creación de la lista de detalles de la factura
     * - Creación de un rol de usuario de prueba
     * - Creación de un usuario receiver de factura
     * - Creación de un usuario asociado a la factura con un rol asignado.
     * - Simulación de la URL base de la solicitud HTTP.
     */
    @BeforeEach
    void setUp() {
        testInvoice = new Invoice();
        testInvoice.setId(1L);
        testInvoice.setConsecutive("00100001000");
        testInvoice.setIssueDate(LocalDate.now());
        testInvoice.setType("Ingreso");
        testInvoice.setInvoiceKey("50615");
        testInvoice.setUser(testUser);
        testInvoice.setReceiver(testInvoiceUser);
        testInvoice.setDetails(testDetails);

        testDetails = new ArrayList<>();
        DetailsInvoice detailsInvoice = new DetailsInvoice();
        detailsInvoice.setId(1L);
        detailsInvoice.setCabys("1234");
        detailsInvoice.setDescription("Servicio de desarrollo web");
        detailsInvoice.setQuantity(2);
        detailsInvoice.setUnitPrice(5000);
        detailsInvoice.setUnit("Unidad");
        detailsInvoice.setDiscount(0);
        detailsInvoice.setTax(13);
        detailsInvoice.setCategory("Servicios");
        detailsInvoice.setTaxAmount(1300);
        detailsInvoice.setTotal(11300);
        testDetails.add(detailsInvoice);

        testRole = new Role();
        testRole.setName(RoleEnum.USER);

        testInvoiceUser = new InvoiceUser();
        testInvoiceUser.setId(1L);
        testInvoiceUser.setName("Diego");
        testInvoiceUser.setLastName("Nunez");
        testInvoiceUser.setIdentification("101110111");
        testInvoiceUser.setEmail("diego@gmail.com");


        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(testRole);

        StringBuffer reqURL = new StringBuffer("http://localhost");
        when(httpServletRequest.getRequestURL()).thenReturn(reqURL);
    }

    /**
     * Prueba unitaria para el método "createInvoice" de la clase "InvoiceRestController",
     * encargada de verificar que se pueda registrar una factura cuando se proporcionan datos válidos.
     *
     * La prueba unitaria realiza las siguientes acciones:
     * - Simula la llamada al servicio "InvoiceService" para guardar la factura asociada al usuario.
     * - Simula el guardado de la factura en el repositorio
     * - Verifica que se devuelva un código de estado HTTP 201 (CREATED) si la factura se registra exitosamente.
     * - Verifica que el método "saveInvoice" del servicio "invoiceService" sea llamado con los parámetros correctos.
     */
    @Test
    @DisplayName("Debe crear una factura")
    void addInvoice_WithValidData_ShouldReturnCreated() {
        when(invoiceService.saveInvoice(any(Invoice.class), eq(testUser.getId()))).thenReturn(testInvoice);

        ResponseEntity<?> response = invoiceRestController.createInvoice(testInvoice, testUser, httpServletRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(invoiceService).saveInvoice(any(Invoice.class), eq(testUser.getId()));
    }

    /**
     * Prueba unitaria para el método "getAll" de la clase "InvoiceRestController",
     * encargada de verificar que se pueda obtener una lista paginada de facturas cuando existen registros.
     *
     * La prueba unitaria realiza las siguientes acciones:
     * - Simula la respuesta del repositorio "invoiceRepository" para devolver una lista paginada con una factura de prueba asociada al usuario.
     * - Simula la llamada al método "findByUserId" del repositorio con un objeto Pageable.
     * - Verifica que se devuelva un código de estado HTTP 200 (OK) si la lista de facturas se obtiene correctamente.
     * - Verifica que el método "findByUserId" del "invoiceRepository" sea llamado con los parámetros correctos.
     */
    @Test
    @DisplayName("Debe retornar una lista paginada con todas las facturas")
    void getAllInvoices_WhenInvoicesExist_ShouldReturnPaginatedList() {
        Page<Invoice> invoicesPage = new PageImpl<>(Collections.singletonList(testInvoice));
        when(invoiceRepository.findByUserId(eq(testUser.getId()), any(Pageable.class)))
                .thenReturn(invoicesPage);

        ResponseEntity<?> response = invoiceRestController.getAll(1, 5, "", testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(invoiceRepository).findByUserId(eq(testUser.getId()), any(Pageable.class));
    }

    /**
     * Prueba unitaria para el método "updateInvoice" de la clase "InvoiceRestController",
     * encargada de verificar que se pueda actualizar una factura cuando el ID proporcionado es válido.
     *
     * La prueba unitaria realiza las siguientes acciones:
     * - Simula la llamada al servicio "invoiceService" para guardar la factura actualizada asociada al usuario.
     * - Simula el retorno de la factura actualizada.
     * - Verifica que se devuelva un código de estado HTTP 200 (OK) si la factura se actualiza correctamente.
     * - Verifica que el método "saveInvoice" del servicio "invoiceService" sea llamado con los parámetros correctos.
     */
    @Test
    @DisplayName("Debe actualizar una factura cuando el ID proporcionado es válido")
    void updateInvoice_WithExistingId_ShouldReturnOk() {
        when(invoiceService.saveInvoice(any(Invoice.class), eq(testUser.getId()))).thenReturn(testInvoice);

        ResponseEntity<?> response = invoiceRestController.updateInvoice(1L, testInvoice, testUser, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(invoiceService).saveInvoice(any(Invoice.class), eq(testUser.getId()));
    }

    /**
     * Prueba unitaria para el método "deleteInvoice" de la clase "InvoiceRestController",
     * encargada de verificar que se pueda eliminar una factura cuando el ID proporcionado es válido.
     *
     * La prueba unitaria realiza las siguientes acciones:
     * - Simula la llamada al servicio "invoiceService" para eliminar la factura por ID.
     * - Simula la ejecución de la eliminación sin errores.
     * - Verifica que se devuelva un código de estado HTTP 200 (OK) si la factura se elimina correctamente.
     * - Verifica que el método "deleteInvoice" del servicio "invoiceService" sea llamado con el ID correcto.
     */
    @Test
    @DisplayName("Debe eliminar una factura cuando el ID proporcionado es válido")
    void deleteInvoice_WithExistingId_ShouldReturnOk() {
        doNothing().when(invoiceService).deleteInvoice(1L);

        ResponseEntity<?> response = invoiceRestController.deleteInvoice(1L, httpServletRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(invoiceService).deleteInvoice(1L);
    }

}
