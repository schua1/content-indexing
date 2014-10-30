package org.deshang.content.indexing.config;

import java.sql.Driver;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:/indexing-spring.properties")
public class SpringConfig {

    private final String C3P0_POOL_PROPERTIE_MIN_SIZE = "c3p0.pool.min.size";
    private final String C3P0_POOL_PROPERTIE_MAX_SIZE = "c3p0.pool.max.size";
    private final String C3P0_POOL_PROPERTIE_CHECKOUT_TIMEOUT = "c3p0.pool.checkout.timeout.second";
    private final String C3P0_POOL_PROPERTIE_MAX_STATEMENTS = "c3p0.pool.max.statements";

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass(env.getPropertyAsClass("db.jdbc.driver.class", Driver.class).getName());
        dataSource.setJdbcUrl(env.getProperty("db.jdbc.url"));
        dataSource.setUser(env.getProperty("db.username"));
        dataSource.setPassword(env.getProperty("db.password"));
        dataSource.setMinPoolSize(env.getProperty(C3P0_POOL_PROPERTIE_MIN_SIZE, Integer.class));
        dataSource.setMaxPoolSize(env.getProperty(C3P0_POOL_PROPERTIE_MAX_SIZE, Integer.class));
        dataSource.setCheckoutTimeout(env.getProperty(C3P0_POOL_PROPERTIE_CHECKOUT_TIMEOUT, Integer.class));
        dataSource.setMaxStatements(env.getProperty(C3P0_POOL_PROPERTIE_MAX_STATEMENTS, Integer.class));

        return dataSource;
    }

    @Bean
    public JdbcTemplate setupJdbcTemplate(DataSource dataSource) throws Exception {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) throws Exception {
        return new DataSourceTransactionManager(dataSource);
    }
}