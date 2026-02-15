package com.example.batch.repo.mysql;

import com.example.batch.entity.mysql.MySQLEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MySQLEmployeeRepository extends JpaRepository<MySQLEmployee, Long> {
}
