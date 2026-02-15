package com.example.batch.service;

import com.example.batch.entity.mysql.MySQLEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MySQLRetrievalService {

    Page<MySQLEmployee> getEmployeesPage(Pageable pageable);
    Slice<MySQLEmployee> getEmployeesSlice(Pageable pageable);
    List<MySQLEmployee> getKeysetPage(Long lastId, int size);
    List<MySQLEmployee> getAllUsingKeyset();

}