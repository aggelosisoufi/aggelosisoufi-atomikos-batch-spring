package com.example.batch.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.batch.repo.mysql",
        entityManagerFactoryRef = "mySQLEntityManager",
        transactionManagerRef = "transactionManager"
)
public class MySQLDataSourceConfig {
    @Bean(name = "mySQLDataSource", initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix = "mysql.datasource")
    public AtomikosDataSourceBean mySQLDataSource() {
        return new AtomikosDataSourceBean();
    }

    @Bean(name = "mySQLEntityManager")
    public EntityManagerFactory mySQLEntityManager(@Qualifier("mySQLDataSource") DataSource mySQLDataSource) {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJtaDataSource(mySQLDataSource);
        factory.setPackagesToScan("com.example.batch.entity.mysql");
        factory.setPersistenceUnitName("mySQLPersistenceUnit");

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
}
