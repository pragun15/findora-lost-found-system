package com.lostandfound.findora.controller;

import com.lostandfound.findora.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/csv")
    public void exportCsv(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer categoryId,
            HttpServletResponse response
    ) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"items_export.csv\"");

        byte[] data = exportService.generateCsv(status, categoryId);
        response.getOutputStream().write(data);
    }
}
