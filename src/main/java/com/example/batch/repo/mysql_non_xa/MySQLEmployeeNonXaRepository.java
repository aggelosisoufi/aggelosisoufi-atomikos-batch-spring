package com.example.batch.repo.mysql_non_xa;

import com.example.batch.entity.mysql.MySQLEmployee;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MySQLEmployeeNonXaRepository extends JpaRepository<MySQLEmployee, Long> {

    Slice<MySQLEmployee> findAllBy(Pageable pageable);
    List<MySQLEmployee> findByIdGreaterThanOrderByIdAsc(Long lastId, Pageable pageable);
    Window<MySQLEmployee> findByOrderByIdAsc(ScrollPosition position, int size);
}