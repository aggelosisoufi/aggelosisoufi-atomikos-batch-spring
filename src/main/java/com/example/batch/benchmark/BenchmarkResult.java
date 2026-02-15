package com.example.batch.benchmark;

import lombok.Data;

import java.util.List;

@Data
public class BenchmarkResult {
    private String method;                   // OFFSET or KEYSET
    private long totalTimeMs;                // total for full pagination
    private List<Long> perPageTimesMs;       // one entry per page
}
