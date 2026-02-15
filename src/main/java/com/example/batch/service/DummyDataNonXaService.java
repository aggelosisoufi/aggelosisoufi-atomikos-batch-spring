package com.example.batch.service;

public interface DummyDataNonXaService {
    void insertBatchNonXa(int count, int flushingBatchSize, int hibernateJdbcBatchSize);
}
