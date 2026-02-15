package com.example.batch.service;

import com.example.batch.entity.mysql.MySQLEmployee;
import com.example.batch.repo.mysql_non_xa.MySQLEmployeeNonXaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.WindowIterator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MySQLRetrievalServiceImpl implements MySQLRetrievalService {

    private final MySQLEmployeeNonXaRepository repo;

    @Override
    @Transactional(value = "mysqlNonXaTxManager", readOnly = true)
    public Page<MySQLEmployee> getEmployeesPage(Pageable pageable) {
        return repo.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("lastName").ascending()
                )
        );
    }

    @Override
    @Transactional(value = "mysqlNonXaTxManager", readOnly = true)
    public Slice<MySQLEmployee> getEmployeesSlice(Pageable pageable) {
        return repo.findAllBy(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        Sort.by("lastName").ascending()
                )
        );
    }

    @Override
    @Transactional(value = "mysqlNonXaTxManager", readOnly = true)
    public List<MySQLEmployee> getKeysetPage(Long lastId, int size) {

        if (lastId == null) lastId = 0L;

        return repo.findByIdGreaterThanOrderByIdAsc(lastId, PageRequest.of(0, size));
    }

    @Override
    @Transactional(value = "mysqlNonXaTxManager", readOnly = true)
    public List<MySQLEmployee> getAllUsingKeyset() {

        WindowIterator<MySQLEmployee> iterator =
                WindowIterator.of(position ->
                        repo.findByOrderByIdAsc(position, 500)
                ).startingAt(ScrollPosition.keyset());

        List<MySQLEmployee> result = new ArrayList<>();
        iterator.forEachRemaining(result::add);

        return result;
    }
}
