package com.example.batch.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.batch.repo.mysql_non_xa",
        entityManagerFactoryRef = "mysqlNonXaEmf",
        transactionManagerRef = "mysqlNonXaTxManager"
)
public class MySQLNonXaConfig {

    @Bean(name = "mysqlNonXaDataSource")
    @ConfigurationProperties(prefix = "mysql.non-xa.datasource")
    public DataSource mysqlNonXaDataSource() {
        return new HikariDataSource();
    }

    @Bean(name = "mysqlNonXaEmf")
    public EntityManagerFactory mysqlNonXaEmf(
            @Qualifier("mysqlNonXaDataSource") DataSource ds
    ) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();

        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setDataSource(ds);
        factory.setPackagesToScan("com.example.batch.entity.mysql");
        factory.setPersistenceUnitName("mysqlNonXaPU");

        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.hbm2ddl.auto", "update");
        props.put("hibernate.jdbc.batch_size", 50);
        props.put("hibernate.order_inserts", true);
        props.put("hibernate.order_updates", true);
        props.put("hibernate.generate_statistics", true);
        props.put("hibernate.jdbc.batch_versioned_data", true);

        factory.setJpaPropertyMap(props);
        factory.afterPropertiesSet();

        return factory.getObject();
    }

    @Bean(name = "mysqlNonXaTxManager")
    public PlatformTransactionManager mysqlNonXaTxManager(
            @Qualifier("mysqlNonXaEmf") EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}