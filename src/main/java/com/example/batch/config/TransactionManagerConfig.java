package com.example.batch.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
@EnableTransactionManagement
public class TransactionManagerConfig {

    @Bean(initMethod = "init", destroyMethod = "close")
    public UserTransactionManager atomikosTransactionManager() throws SystemException {
        UserTransactionManager txManager = new UserTransactionManager();
        txManager.setForceShutdown(false); // prefer graceful shutdown
        txManager.setTransactionTimeout(300);
        return txManager;
    }

    @Bean
    public UserTransaction atomikosUserTransaction() throws SystemException {
        UserTransactionImp userTx = new UserTransactionImp();
        userTx.setTransactionTimeout(300);
        return userTx;
    }

    @Bean
    @Primary
    public JtaTransactionManager transactionManager() throws SystemException {
        return new JtaTransactionManager(atomikosUserTransaction(), atomikosTransactionManager());
    }
}