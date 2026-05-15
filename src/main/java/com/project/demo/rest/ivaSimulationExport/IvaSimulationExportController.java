package com.project.demo.rest.ivaSimulationExport;

import com.project.demo.logic.entity.ivaCalculation.IvaCalculation;
import com.project.demo.logic.entity.ivaCalculation.IvaCalculationRepository;
import com.project.demo.logic.entity.ivaCalculation.IvaExportService;
import com.project.demo.rest.simulationExport.SimulationExportController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/iva")
public class IvaSimulationExportController extends SimulationExportController<IvaCalculation, Long> {

    @Autowired
    private IvaCalculationRepository ivaRepository;

    @Autowired
    private IvaExportService ivaExportService;

    @Override
    protected Optional<IvaCalculation> findById(Long id) {
        return ivaRepository.findById(id);
    }

    @Override
    protected String generatePdfContent(IvaCalculation simulation) {
        return ivaExportService.generateSimulationPdf(simulation);
    }

    @Override
    protected String generateCsvContent(IvaCalculation simulation) {
        return ivaExportService.generateCsv(simulation);
    }

    @Override
    protected String getPdfFileName() {
        return "Simulacion_IVA.pdf";
    }

    @Override
    protected String getCsvFileName() {
        return "Simulacion_IVA.csv";
    }

}
