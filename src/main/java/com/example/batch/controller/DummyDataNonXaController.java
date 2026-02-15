package com.example.batch.controller;

import com.example.batch.service.DummyDataNonXaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/non-xa")
@RequiredArgsConstructor
public class DummyDataNonXaController {

    private final DummyDataNonXaService service;

    @PostMapping("/generate")
    public String generate(@RequestParam(defaultValue = "50000") int count,
                           @RequestParam(defaultValue = "10000") int flushingBatchSize,
                           @RequestParam(defaultValue = "50") int hibernateJdbcBatchSize) {
        service.insertBatchNonXa(count, flushingBatchSize, hibernateJdbcBatchSize);
        return "Inserted (non-XA) " + count + " rows into MySQL";
    }
}
