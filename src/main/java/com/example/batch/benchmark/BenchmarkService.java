package com.example.batch.benchmark;

import com.example.batch.entity.mysql.MySQLEmployee;
import com.example.batch.service.MySQLRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private final MySQLRetrievalService retrievalService;

    public List<BenchmarkResult> runPageOffsetBenchmark() {
        warmup("PAGE-OFFSET");
        return repeatRuns("PAGE-OFFSET");
    }

    public List<BenchmarkResult> runSliceOffsetBenchmark() {
        warmup("SLICE-OFFSET");
        return repeatRuns("SLICE-OFFSET");
    }

    public List<BenchmarkResult> runKeysetCursorBenchmark() {
        warmup("KEYSET-CURSOR");
        return repeatRuns("KEYSET-CURSOR");
    }

    public void exportPageOffsetCsv(List<BenchmarkResult> results) {
        CsvExporter.writeCsv("page_offset_results.csv", results);
    }

    public void exportSliceOffsetCsv(List<BenchmarkResult> results) {
        CsvExporter.writeCsv("slice_offset_results.csv", results);
    }

    public void exportKeysetCursorCsv(List<BenchmarkResult> results) {
        CsvExporter.writeCsv("keyset_cursor_results.csv", results);
    }

    // BENCHMARK CORE LOGIC
    private void warmup(String method) {
        log.info("🔥 Performing {} warm-up runs for {} ...", BenchmarkConfig.WARMUP_RUNS, method);

        for (int i = 1; i <= BenchmarkConfig.WARMUP_RUNS; i++) {
            log.info("   Warm-up {} / {}", i, BenchmarkConfig.WARMUP_RUNS);

            switch (method) {
                case "PAGE-OFFSET" -> runPageOffsetOnce();
                case "SLICE-OFFSET" -> runSliceOffsetOnce();
                case "KEYSET-CURSOR" -> runKeysetCursorOnce();
            }
        }

        log.info("🔥 Warm-up completed for {}", method);
    }

    private List<BenchmarkResult> repeatRuns(String method) {
        List<BenchmarkResult> results = new ArrayList<>();

        log.info("▶️ Starting real benchmark runs for {} ...", method);

        for (int i = 1; i <= BenchmarkConfig.BENCHMARK_RUNS; i++) {
            log.info("   Run {} / {}", i, BenchmarkConfig.BENCHMARK_RUNS);

            BenchmarkResult result = switch (method) {
                case "PAGE-OFFSET" -> runPageOffsetOnce();
                case "SLICE-OFFSET" -> runSliceOffsetOnce();
                case "KEYSET-CURSOR" -> runKeysetCursorOnce();
                default -> throw new IllegalArgumentException("Unknown method");
            };

            results.add(result);
        }

        log.info("✔ Benchmark finished for {}", method);

        return results;
    }

    // PAGE OFFSET IMPLEMENTATION
    private BenchmarkResult runPageOffsetOnce() {
        // Calculate the total time for retrieving all pages (total items / page size)
        long startTotal = System.currentTimeMillis();
        List<Long> pageTimes = new ArrayList<>();

        int page = 0;

        while (true) {

            // Measure time for each page retrieval
            long startPage = System.currentTimeMillis();

            Page<MySQLEmployee> p =
                    retrievalService.getEmployeesPage(
                            PageRequest.of(page, BenchmarkConfig.PAGE_SIZE)
                    );

            long endPage = System.currentTimeMillis();
            pageTimes.add(endPage - startPage);

            if (!p.hasNext()) break;
            page++;
        }

        BenchmarkResult result = new BenchmarkResult();
        result.setMethod("PAGE-OFFSET");
        result.setTotalTimeMs(System.currentTimeMillis() - startTotal);
        result.setPerPageTimesMs(pageTimes);

        return result;
    }

    // SLICE OFFSET IMPLEMENTATION
    private BenchmarkResult runSliceOffsetOnce() {
        long startTotal = System.currentTimeMillis();
        List<Long> pageTimes = new ArrayList<>();

        int page = 0;

        while (true) {

            long startPage = System.currentTimeMillis();

            Slice<MySQLEmployee> slice =
                    retrievalService.getEmployeesSlice(
                            PageRequest.of(page, BenchmarkConfig.PAGE_SIZE)
                    );

            long endPage = System.currentTimeMillis();
            pageTimes.add(endPage - startPage);

            if (!slice.hasNext()) break;
            page++;
        }

        BenchmarkResult result = new BenchmarkResult();
        result.setMethod("SLICE-OFFSET");
        result.setTotalTimeMs(System.currentTimeMillis() - startTotal);
        result.setPerPageTimesMs(pageTimes);

        return result;
    }

    // KEYSET CURSOR IMPLEMENTATION
    private BenchmarkResult runKeysetCursorOnce() {
        long startTotal = System.currentTimeMillis();
        List<Long> pageTimes = new ArrayList<>();

        Long lastId = null;

        while (true) {

            long startPage = System.currentTimeMillis();

            List<MySQLEmployee> rows =
                    retrievalService.getKeysetPage(lastId, BenchmarkConfig.PAGE_SIZE);

            long endPage = System.currentTimeMillis();
            pageTimes.add(endPage - startPage);

            if (rows.isEmpty()) break;
            lastId = rows.getLast().getId();
        }

        BenchmarkResult result = new BenchmarkResult();
        result.setMethod("KEYSET-CURSOR");
        result.setTotalTimeMs(System.currentTimeMillis() - startTotal);
        result.setPerPageTimesMs(pageTimes);

        return result;
    }
}