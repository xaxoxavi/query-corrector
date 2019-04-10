package com.esliceu;

import com.esliceu.extractor.QueryExtractor;
import com.esliceu.extractor.QueryWrapper;
import com.esliceu.report.QueryReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xavi on 5/03/18.
 */
@Component
public class QueryCorrector {

    private final QueryAnalyzer queryAnalyzer;
    private final QueryExtractor queryExtractor;


    @Autowired
    private ApplicationContext applicationContext;


    @Autowired
    public QueryCorrector(QueryAnalyzer queryAnalyzer, QueryExtractor queryExtractor) {
        this.queryAnalyzer = queryAnalyzer;
        this.queryExtractor = queryExtractor;
    }

    public void doStuff() {

        Map<Integer, Set<String>> queryRepo = new HashMap<>();

        List<Result> resultsTeacher = queryAnalyzer.analyzeQueries(queryExtractor.extractSolutionQueries());

        List<QueryWrapper> queryWrapperList = queryExtractor.extractQueries();

        QueryReport queryReport = applicationContext.getBean(QueryReport.class);

        for (QueryWrapper queryWrapper : queryWrapperList) {

            queryReport.addStudent("STUDENT: " + queryWrapper.getStudent());

            List<Result> results = queryAnalyzer.analyzeQueries(queryWrapper.getQueries());


            for (int i = 0; i < results.size(); i++) {
                queryReport.prepareNewResult();

                try {
                    Result resultTeacher = resultsTeacher.get(i);
                    Result result = results.get(i);

                    //save query to repo
                    queryRepo.computeIfAbsent(i, k -> new HashSet<>());
                    queryRepo.get(i).add(result.getQuery());


                    queryReport.setQuery(result.getQuery());
                    queryReport.setIndex(i+1);

                    if (ResultStatus.IGNORED.equals(resultTeacher.getStatus())) {
                        queryReport.setStatus(ResultStatus.IGNORED);
                        continue;
                    }


                    if (!result.getRowNumber().equals(resultTeacher.getRowNumber())) {

                        queryReport.setExecuteError(ResultStatus.EXECUTE_ERROR,
                                resultTeacher.getRowNumber(),
                                result.getRowNumber(),
                                result.getErrorMessage());
                        continue;
                    }

                    if (ResultStatus.PARSE_ERROR.equals(result.getStatus())) {
                        queryReport.setErrorMessage(result.getParseErrorMessage());
                        queryReport.setStatus(ResultStatus.PARSE_ERROR);
                        continue;
                    }



                    if (resultTeacher.getTables().size() < result.getTables().size()
                            || differentTables(resultTeacher.getTables(), result.getTables()) ){
                        queryReport.setStatus(ResultStatus.DIF_TABLES);
                        queryReport.setStudentTables(result.getTables());
                        queryReport.setTeacherTables(resultTeacher.getTables());
                        continue;
                    }


                    queryReport.setStatus(ResultStatus.PARSE_OK);


                    Set<String> differentFields = resultTeacher.getFields().stream()
                            .filter(s -> !result.getFields().contains(s))
                            .filter(s -> !s.contains(".") || !result.getFields().contains(s.split("[.]")[1]))
                            .collect(Collectors.toSet());


                    if (differentFields.size() > 0) {

                        queryReport.setTeacherFields(resultTeacher.getFields());
                        queryReport.setStudentFields(result.getFields());

                    }


                    if (compareResults(resultTeacher, result)) {
                        queryReport.setStatus(ResultStatus.PARSE_OK);
                    }


                } catch (IndexOutOfBoundsException ex) {

                    queryReport.setStatus(ResultStatus.EXECUTE_ERROR);
                    queryReport.setErrorMessage(ex.getMessage());

                }


            }


        }

        queryReport.doReport();

        queryRepo.keySet().stream().forEach(i -> {
            Path newFilePath = Paths.get("/home/xavi/bbdd/results/" + (i+1) + ".out");
            try {

                if (!Files.exists(newFilePath)) {
                    newFilePath = Files.createFile(newFilePath);
                }

                Path finalNewFilePath = newFilePath;
                queryRepo.get(i).stream().forEach(query -> {
                    try {
                        query += "\n";
                        Files.write(finalNewFilePath, query.getBytes(), StandardOpenOption.APPEND);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean differentTables(Set<String> teacherTables, Set<String> studentTables) {

        List<String> differentTables = teacherTables.stream().filter(s -> ! studentTables.contains(s)).collect(Collectors.toList());

        return differentTables.size() > 0;

    }

    private boolean compareResults(Result resultTeacher, Result result) {

        if (ResultStatus.IGNORED.equals(resultTeacher.getStatus()))
            return resultTeacher.getStatus().equals(result.getStatus());

        if (resultTeacher.getResultRows().size() != result.getResultRows().size()) return false;

        for (String row : resultTeacher.getResultRows()) {
            if (!result.getResultRows().contains(row)) {
                return false;
            }
        }

        return true;
    }


}
