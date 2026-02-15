package com.example.batch.controller;

import com.example.batch.entity.mysql.MySQLEmployee;
import com.example.batch.service.DummyDataGeneratorService;
import com.example.batch.service.MySQLRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DummyDataController {

    private final DummyDataGeneratorService generatorService;
    private final MySQLRetrievalService mysqlRetrievalService;

    @PostMapping("/generate")
    public String generateDummyData(@RequestParam(defaultValue = "25000") int count) {
        generatorService.generateAndSaveDummyData(count);
        return "Generated and saved " + count + " dummy records in both databases.";
    }

    @GetMapping("/page")
    public Page<MySQLEmployee> page(Pageable pageable) {
        return mysqlRetrievalService.getEmployeesPage(pageable);
    }

    @GetMapping("/slice")
    public Slice<MySQLEmployee> slice(Pageable pageable) {
        return mysqlRetrievalService.getEmployeesSlice(pageable);
    }

    @GetMapping("/keyset")
    public List<MySQLEmployee> keyset(
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "500") int size
    ) {
        return mysqlRetrievalService.getKeysetPage(lastId, size);
    }

    @GetMapping("/window-keyset")
    public List<MySQLEmployee> windowKeyset() {
        return mysqlRetrievalService.getAllUsingKeyset();
    }
}

