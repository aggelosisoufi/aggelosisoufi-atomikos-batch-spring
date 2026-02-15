package com.example.batch.service;

import com.example.batch.repo.mysql.MySQLEmployeeRepository;
import com.example.batch.repo.postgres.PostgresEmployeeRepository;
import com.example.batch.utils.DummyDataGeneratorUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DummyDataGeneratorServiceImpl implements DummyDataGeneratorService{
    private static final Logger logger = LoggerFactory.getLogger(DummyDataGeneratorServiceImpl.class);

    private final MySQLEmployeeRepository mysqlRepo;
    private final PostgresEmployeeRepository postgresRepo;

    @Transactional("transactionManager")
    public void generateAndSaveDummyData(int count) {
        var mysqlEmployees = DummyDataGeneratorUtil.generateMySQLEmployees(count);
        var postgresEmployees = DummyDataGeneratorUtil.generatePostgresEmployees(count);

        logger.info("Saving {} MySQL employees and {} Postgres employees", mysqlEmployees.size(), postgresEmployees.size());

        mysqlRepo.saveAll(mysqlEmployees);
        postgresRepo.saveAll(postgresEmployees);
    }
}
