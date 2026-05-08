package com.lostandfound.findora.service;

import com.lostandfound.findora.model.Item;
import com.lostandfound.findora.repository.ItemRepository;
import com.lostandfound.findora.repository.ItemSpecification;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ExportServiceImpl implements ExportService {

    private final ItemRepository itemRepository;

    public ExportServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public byte[] generateCsv(String status, Integer categoryId) throws IOException {
        Specification<Item> spec = ItemSpecification.buildSpec(
                null, status, categoryId, null, null, null, null
        );
        List<Item> items = itemRepository.findAll(spec);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "ID", "Title", "Description", "Category", "Color",
                "Keywords", "Status", "Location", "Date Reported", "Reporter"
        ));

        for (Item item : items) {
            printer.printRecord(
                    item.getId(),
                    item.getTitle(),
                    item.getDescription(),
                    item.getCategory() != null ? item.getCategory().getName() : "",
                    item.getColor(),
                    item.getKeywords(),
                    item.getStatus(),
                    item.getLocation(),
                    item.getDateReported(),
                    item.getReporter() != null ? item.getReporter().getName() : ""
            );
        }

        printer.flush();
        return out.toByteArray();
    }
}
