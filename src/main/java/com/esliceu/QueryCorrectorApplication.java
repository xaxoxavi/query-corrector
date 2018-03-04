package com.esliceu;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
public class QueryCorrectorApplication {

    Logger log = LoggerFactory.getLogger(QueryCorrectorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(QueryCorrectorApplication.class, args);
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/sakila");
        dataSource.setUsername("root");
        dataSource.setPassword("test");
        return dataSource;
    }


    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    CommandLineRunner runner(QueryParser queryParser) {
        return args -> {

            Map<Integer, Set<String>> queryRepo = new HashMap<>();

            File folder = new File("/home/xavi/bbdd");

            List<Result> resultsPractica = queryParser.correct("practica.sql");

            for (File subFolder : folder.listFiles()) {

                log.info("STUDENT: " + subFolder.getName());

                if (subFolder.isDirectory()) {

                    for (File sqlFile : subFolder.listFiles()) {

                        if (isValidFile(sqlFile)) {

                            List<Result> results = queryParser.correct(sqlFile);

                            if (results.size() != resultsPractica.size()) log.error("ERROR: Número de querys erroni");
                            else log.info("Numero de querys: " + resultsPractica.size());


                            int correct = 0;
                            for (int i = 0; i < results.size(); i++) {

                                Result resultPractica = resultsPractica.get(i);
                                Result result = results.get(i);

                                log.info("-------------------------------------" + i + "-------------------------------------");

                                if (!result.compareTo(resultsPractica.get(i))) {
                                    log.error("ERROR #" + i);
                                    log.error(result.getQuery());
                                    log.error("ERROR MsG: "+  result.getErrorMessage());
                                } else {



                                    if (!ResultStatus.PARSE_ERROR.equals(result.getStatus())) {

                                        if (resultPractica.getTables().size() >= result.getTables().size()
                                            && result.getTables().containsAll(resultPractica.getTables())) {
                                            correct++;
                                            //save query to repo
                                            queryRepo.computeIfAbsent(i, k -> new HashSet<>());
                                            queryRepo.get(i).add(result.getQuery());


                                        } else {
                                            log.error("DIFERENT TABLES #"+i );
                                            log.error("TEACHER-> " + resultPractica.getTables().size());
                                            resultPractica.getTables().stream().forEach(t-> log.error(t));
                                            log.error("STUDENT-> " +result.getTables().size());
                                            result.getTables().stream().forEach(t-> log.error(t));
                                        }

                                        Set<String> differentFields = resultPractica.getFields().stream()
                                                .filter(s -> !result.getFields().contains(s) )
                                                .filter(s -> !s.contains(".") || !result.getFields().contains(s.split("[.]")[1]))
                                                .collect(Collectors.toSet());


                                        if (differentFields.size() > 0) {
                                            log.warn("DIFERENT FIELDS: #" + i);
                                            differentFields.stream().forEach(field -> log.warn(field));

                                            log.warn("STUDENT FIELDS: #" + i);
                                            result.getFields().stream().forEach(field -> log.warn(field));
                                            log.warn("TEACH FIELDS:");
                                            resultPractica.getFields().stream().forEach(field -> log.warn(field));
                                        }


                                    } else {

                                        log.warn("*** PARSE ERROR #" + i + " ********** ");
                                        log.warn(result.getParseErrorMessage());

                                    }


                                }
                                log.info("---------------------------------------------------------------------------");
                            }

                            if (results.size()>0){
                                int nota = correct * 100 / resultsPractica.size();

                                log.info("NOTA FINAL: " + nota);

                            }

                            log.info("#################################################################");
                            log.info("#################################################################");




                        }

                    }

                }
            }


            queryRepo.keySet().stream().forEach(i ->{
                Path newFilePath = Paths.get("/home/xavi/bbdd/results/" + i + ".out");
                try {
                    Path path = Files.createFile(newFilePath);

                    queryRepo.get(i).stream().forEach(query -> {
                        try {
                            query += "\n";
                            Files.write(path, query.getBytes(), StandardOpenOption.APPEND);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });



        };
    }



    private boolean isValidFile(File sqlFile) {

        String name = sqlFile.getName();

        return sqlFile.isFile() &&
                (FilenameUtils.getExtension(name).equals("sql") ||
                        FilenameUtils.getExtension(name).equals("txt"));
    }


}
