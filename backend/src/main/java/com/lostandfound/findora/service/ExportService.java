package com.lostandfound.findora.service;

import java.io.IOException;

public interface ExportService {

    byte[] generateCsv(String status, Integer categoryId) throws IOException;
}
