/*
 * Copyright 2014 Deshang group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.deshang.content.indexing.config;

import java.sql.Driver;

import javax.sql.DataSource;

import org.deshang.content.indexing.scheduling.ContentIndexingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@EnableScheduling
@PropertySource("classpath:/indexing-spring.properties")
public class SpringConfig {

    private final String CONTENT_INDEXING_TASK_CRON_EXPRESSION = "content.indexing.task.cron.expression";

    private final String CONTENT_INDEX_PATH = "content.index.root.path";

    private final String C3P0_POOL_PROPERTIE_MIN_SIZE = "c3p0.pool.min.size";
    private final String C3P0_POOL_PROPERTIE_MAX_SIZE = "c3p0.pool.max.size";
    private final String C3P0_POOL_PROPERTIE_CHECKOUT_TIMEOUT = "c3p0.pool.checkout.timeout.second";
    private final String C3P0_POOL_PROPERTIE_MAX_STATEMENTS = "c3p0.pool.max.statements";

    private final String DB_PASSWORD = "db.password";
    private final String DB_USERNAME = "db.username";
    private final String DB_JDBC_URL = "db.jdbc.url";
    private final String DB_JDBC_DRIVER_CLASS = "db.jdbc.driver.class";

    private final String SCHEDULER_POOL_SIZE = "schduler.pool.size";

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();

        dataSource.setDriverClass(env.getPropertyAsClass(DB_JDBC_DRIVER_CLASS, Driver.class).getName());
        dataSource.setJdbcUrl(env.getProperty(DB_JDBC_URL));
        dataSource.setUser(env.getProperty(DB_USERNAME));
        dataSource.setPassword(env.getProperty(DB_PASSWORD));
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

    @Bean
    public Runnable contentIndexingTask() throws Exception {
        return new ContentIndexingTask(env.getProperty(CONTENT_INDEX_PATH));
    }

    @Bean
    public TaskScheduler taskScheduler() throws Exception {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(env.getProperty(SCHEDULER_POOL_SIZE, int.class));
        taskScheduler.initialize();
        taskScheduler.schedule(contentIndexingTask(), new CronTrigger(env.getProperty(CONTENT_INDEXING_TASK_CRON_EXPRESSION)));
        return taskScheduler;
    }
}