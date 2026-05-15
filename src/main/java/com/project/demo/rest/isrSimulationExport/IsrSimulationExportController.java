package com.project.demo.rest.isrSimulationExport;

import com.project.demo.logic.entity.isrSimulation.IsrRepository;
import com.project.demo.logic.entity.isrSimulation.IsrSimulation;
import com.project.demo.logic.entity.isrSimulation.IsrExportService;
import com.project.demo.rest.simulationExport.SimulationExportController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/isr")
public class IsrSimulationExportController extends SimulationExportController<IsrSimulation, Long> {

    @Autowired
    private IsrRepository isrRepository;

    @Autowired
    private IsrExportService isrExportService;

    @Override
    protected Optional<IsrSimulation> findById(Long id) {
        return isrRepository.findById(id);
    }

    @Override
    protected String generatePdfContent(IsrSimulation simulation) {
        return isrExportService.generateSimulationPdf(simulation);
    }

    @Override
    protected String generateCsvContent(IsrSimulation simulation) {
        return isrExportService.generateSimulationCsv(simulation);
    }

    @Override
    protected String getPdfFileName() {
        return "Simulacion_ISR.pdf";
    }

    @Override
    protected String getCsvFileName() {
        return "Simulacion_ISR.csv";
    }

}


