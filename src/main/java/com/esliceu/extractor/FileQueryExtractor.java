package com.esliceu.extractor;


import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by xavi on 4/03/18.
 */
@Component
public class FileQueryExtractor implements QueryExtractor {

    private Logger log = LoggerFactory.getLogger(FileQueryExtractor.class);

    @Value("${students.queries.path}")
    private String path;

    @Value("${teacher.queries.path}")
    private String teacherPath;

    private Function<String,String> queryCleaner;

    @Override
    public List<Query> extractSolutionQueries(){
        return extractQueries(getFileContent(teacherPath));
    }

    @Override
    public List<QueryWrapper> extractQueries() {

        File folder = new File(path);
        List<QueryWrapper> queryWrapperList = new ArrayList<>();

        for (File subFolder : folder.listFiles()) {

            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.setStudent(subFolder.getName());

            if (subFolder.isDirectory()) {
                for (File sqlFile : subFolder.listFiles()) {
                    if (isValidFile(sqlFile)) {
                       queryWrapper.setQueries(extractQueries(getFileContent(sqlFile)));
                    }
                }
            }

            if (queryWrapper.getQueries() != null){
                queryWrapperList.add(queryWrapper);
            }

        }

        return queryWrapperList;
    }


    private boolean isValidFile(File sqlFile) {

        String name = sqlFile.getName();

        return sqlFile.isFile() &&
                (FilenameUtils.getExtension(name).equals("sql") ||
                        FilenameUtils.getExtension(name).equals("txt"));
    }



    private String getFileContent(File file) {
        StringBuilder data = new StringBuilder();
        try {
            Stream<String> lines = Files.lines(Paths.get(file.getAbsolutePath()));
            lines.forEach(line -> data.append(line).append("\n"));
            lines.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data.toString();
    }

    private String getFileContent(String fileName) {
        StringBuilder data = new StringBuilder();
        try {
            Path path = Paths.get(getClass().getClassLoader()
                    .getResource(fileName).toURI());


            Stream<String> lines = Files.lines(path);
            lines.forEach(line -> data.append(line).append("\n"));
            lines.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return data.toString();
    }

    private List<Query> extractQueries(String fileContent) {

        String[] lines = fileContent.split("\n");

        String[] filteredLines =  Arrays.stream(lines).filter(line->
                (line.contains("#@") ||
                        line.contains("#$") ||
                        !line.contains("#"))
                        && line.trim().length() > 0
        ).toArray(String[]::new);

        List<Query> queries = new ArrayList<>();

        boolean readMode = false;

        StringBuilder sentenceBuilder = new StringBuilder();

        for (String line : filteredLines) {

            if (line.contains("#@")) {
                readMode = true;
                continue;
            }

            if (line.contains("#$")) {
                readMode = false;
                String sentence = sentenceBuilder.toString();
                if (queryCleaner != null) sentence = queryCleaner.apply(sentence);
                Query query = new Query(sentence);
                queries.add(query);
                sentenceBuilder = new StringBuilder();
                continue;
            }

            if (readMode) {
                sentenceBuilder.append(line + " ");
            }
        }

        return queries;
    }

    @Autowired(required = false)
    public void setQueryCleaner(Function<String, String> queryCleaner) {
        this.queryCleaner = queryCleaner;
    }
}
