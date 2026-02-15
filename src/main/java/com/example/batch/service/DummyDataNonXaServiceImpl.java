package com.example.batch.service;

import com.example.batch.entity.mysql.MySQLEmployee;
import com.example.batch.utils.DummyDataGeneratorUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DummyDataNonXaServiceImpl implements DummyDataNonXaService {

    @PersistenceContext(unitName = "mysqlNonXaPU")
    private EntityManager em;

    @Override
    @Transactional("mysqlNonXaTxManager")
    public void insertBatchNonXa(int count, int flushingBatchSize, int hibernateJdbcBatchSize) {

        List<MySQLEmployee> list = DummyDataGeneratorUtil.generateMySQLEmployees(count);

        em.unwrap(Session.class).setJdbcBatchSize(hibernateJdbcBatchSize);
        for (int i = 0; i < list.size(); i++) {
            em.persist(list.get(i));

            if (i % flushingBatchSize == 0) {
                em.flush();
                em.clear();
            }
        }
    }
}