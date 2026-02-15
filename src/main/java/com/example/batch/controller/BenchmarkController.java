package com.example.batch.controller;

import com.example.batch.benchmark.BenchmarkResult;
import com.example.batch.benchmark.BenchmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/benchmark")
@RequiredArgsConstructor
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    // PAGE-OFFSET
    @PostMapping("/page-offset")
    public String runPageOffset() {
        List<BenchmarkResult> results = benchmarkService.runPageOffsetBenchmark();
        benchmarkService.exportPageOffsetCsv(results);
        return "PAGE-OFFSET benchmark complete. CSV generated: page_offset_results.csv";
    }

    // SLICE-OFFSET
    @PostMapping("/slice-offset")
    public String runSliceOffset() {
        List<BenchmarkResult> results = benchmarkService.runSliceOffsetBenchmark();
        benchmarkService.exportSliceOffsetCsv(results);
        return "SLICE-OFFSET benchmark complete. CSV generated: slice_offset_results.csv";
    }

    // KEYSET-CURSOR
    @PostMapping("/keyset-cursor")
    public String runKeysetCursor() {
        List<BenchmarkResult> results = benchmarkService.runKeysetCursorBenchmark();
        benchmarkService.exportKeysetCursorCsv(results);
        return "KEYSET-CURSOR benchmark complete. CSV generated: keyset_cursor_results.csv";
    }
}