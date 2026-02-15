package com.example.batch.benchmark;


import lombok.SneakyThrows;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class CsvExporter {

    @SneakyThrows
    public static void writeCsv(String fileName, List<BenchmarkResult> results) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {

            writer.println("run,method,total_time_ms,page_number,page_time_ms");

            int runIndex = 1;
            for (BenchmarkResult result : results) {
                int pageNum = 1;
                for (Long time : result.getPerPageTimesMs()) {
                    writer.printf("%d,%s,%d,%d,%d%n",
                            runIndex,
                            result.getMethod(),
                            result.getTotalTimeMs(),
                            pageNum,
                            time
                    );
                    pageNum++;
                }
                runIndex++;
            }
        }
    }
}