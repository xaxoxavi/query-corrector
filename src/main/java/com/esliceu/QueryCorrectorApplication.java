package com.esliceu;

import com.esliceu.report.LoggerQueryReport;
import com.esliceu.report.QueryReport;
import com.esliceu.report.SimpleQueryReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.function.Function;

@SpringBootApplication
public class QueryCorrectorApplication {

    Logger log = LoggerFactory.getLogger(QueryCorrectorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(QueryCorrectorApplication.class, args);
    }

    @Value("${datasource.database}")
    private String dataBase;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/" + dataBase);
        dataSource.setUsername("root");
        dataSource.setPassword("test");
        return dataSource;
    }

    @Bean
    //@Scope(value = "prototype", proxyMode = ScopedProxyMode.INTERFACES)
    public QueryReport queryReport(){
        return new LoggerQueryReport();
    }


    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public Function<String,String> queryCleaner(){
        return queryToClean -> queryToClean.replaceAll("(?i)use sakila;", "")
                .trim()
                .replaceAll("[ ]{2,}", " ")
                .replaceAll("(?i)use sanitat;", "");
    }

    @Bean
    CommandLineRunner runner(QueryCorrector queryCorrector) {
        return args -> {

            queryCorrector.doStuff();



        };
    }


}
