package com.advantage.account.test.cfg;

import com.advantage.account.store.config.ImageManagementConfiguration;
import com.advantage.account.store.image.ImageManagement;
import com.advantage.account.store.image.ImageManagementAccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.instrument.classloading.InstrumentationLoadTimeWeaver;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan({"com.advantage.account.store.services",
        "com.advantage.account.store.dao",
        "com.advantage.account.store.user.dao",
        "com.advantage.account.store.user.model",
        "com.advantage.account.store.init"})
@PropertySources(value = {@PropertySource("classpath:imageManagement.properties")})
public class AdvantageTestContextConfiguration {

    @Autowired
    private Environment environment;

    @Bean(name = "transactionManager")
    public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory,
                                                         DriverManagerDataSource dataSource) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean(name = "datasource")
    public DriverManagerDataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(org.hsqldb.jdbcDriver.class.getName());
        dataSource.setUrl("jdbc:hsqldb:mem:mydb");
        dataSource.setUsername("sa");
        dataSource.setPassword("jdbc:hsqldb:mem:mydb");
        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DriverManagerDataSource dataSource) {

        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("com.advantage.account.store.model", "com.advantage.account.store.user.model");
        entityManagerFactoryBean.setLoadTimeWeaver(new InstrumentationLoadTimeWeaver());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> jpaProperties = new HashMap<String, Object>();
        jpaProperties.put("hibernate.hbm2ddl.auto", "create");
        jpaProperties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        jpaProperties.put("hibernate.show_sql", "true");
        jpaProperties.put("hibernate.format_sql", "true");
        jpaProperties.put("hibernate.use_sql_comments", "true");
        entityManagerFactoryBean.setJpaPropertyMap(jpaProperties);

        return entityManagerFactoryBean;
    }

    @Bean(name = "imageManagement")
    public ImageManagement getImageManagement() {

        final String imageManagementRepository = environment.getProperty(ImageManagementConfiguration.PROPERTY_IMAGE_MANAGEMENT_REPOSITORY);
        return ImageManagementAccess.getImageManagement(imageManagementRepository);
    }
}