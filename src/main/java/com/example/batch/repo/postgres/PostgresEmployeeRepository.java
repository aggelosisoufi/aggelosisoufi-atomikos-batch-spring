package com.example.batch.repo.postgres;

import com.example.batch.entity.postgres.PostgresEmployee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostgresEmployeeRepository extends JpaRepository<PostgresEmployee, Long> {
}
