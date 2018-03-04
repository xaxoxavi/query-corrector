package com.esliceu;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by xavi on 26/02/18.
 */
@Component
public class QueryParser {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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


    private List<String> extractQueries(String fileContent) {

        String[] lines = fileContent.split("\n");

        String[] filteredLines =  Arrays.stream(lines).filter(line->
                (line.contains("#@") ||
                 line.contains("#$") ||
                 !line.contains("#"))
                        && line.trim().length() > 0
                ).toArray(String[]::new);

        List<String> queries = new ArrayList<>();

        boolean readMode = false;

        StringBuilder query = new StringBuilder();

        for (String line : filteredLines) {

            if (line.contains("#@")) {
                readMode = true;
                continue;
            }

            if (line.contains("#$")) {
                readMode = false;
                queries.add(query.toString());
                query = new StringBuilder();
                continue;
            }

            if (readMode) {
                query.append(line + " ");
            }
        }

        return queries;
    }

    public List<Result> correct(File sqlFile) throws SQLException {

        List<String> queries = extractQueries(getFileContent(sqlFile));
        return correctQueries(queries);

    }

    public List<Result> correct(String filename) throws SQLException {
        List<String> queries = extractQueries(getFileContent(filename));

        return correctQueries(queries);
    }


    private List<Result> correctQueries(List<String> queries) throws SQLException {


        queries.stream().forEach(query -> System.out.println(query));

        Connection connection = dataSource.getConnection();

        connection.prepareStatement("SET sql_mode='';").execute();

        List<Result> results = new ArrayList<>();

        for (String query : queries){

            System.out.println("#####" + query);

            Result result = new Result();
            Statement statement = null;

            try{
                query = cleanQuery(query);
                result.setQuery(query);

                statement = CCJSqlParserUtil.parse(query);


                Select selectStatement = (Select) statement;

                TableNamesAndAliasFinder tablesNamesFinder = new TableNamesAndAliasFinder();
                List<String> tables = tablesNamesFinder.getTableList(selectStatement);
                tables.forEach(table -> result.addTable(table));

                if (selectStatement.getSelectBody() instanceof PlainSelect){

                    PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

                    extractFields(plainSelect,result,tablesNamesFinder.getAlias());

                } else if (selectStatement.getSelectBody() instanceof SetOperationList) {

                    SetOperationList setOperationList = (SetOperationList) selectStatement.getSelectBody();

                    setOperationList.getPlainSelects().stream().forEach(ps -> extractFields(ps,result,tablesNamesFinder.getAlias()));

                }





            } catch (JSQLParserException e) {
                e.printStackTrace();
                result.setStatus(ResultStatus.PARSE_ERROR);
                result.setParseErrorMessage(e.getCause().getMessage());
            } catch (Throwable ex) {
                ex.printStackTrace();
                result.setStatus(ResultStatus.PARSE_ERROR);
                result.setParseErrorMessage(ex.getMessage());
            }

            try{

                PreparedStatement preparedStatement = connection.prepareStatement(query);

                ResultSet rs = preparedStatement.executeQuery();


                int count = 0;

                while (rs.next()){
                    count++;

                }
                result.setRowNumber(count);

            }catch (Throwable ex){
                ex.printStackTrace();
                result.setErrorMessage(ex.getMessage() + " " + ex.getCause());

            } finally {
                results.add(result);
            }

        }

        connection.close();

        return results;



    }

    private void extractFields(PlainSelect plainSelect, Result result, Map<String,String> aliasMap) {
        plainSelect.getSelectItems().stream().forEach(selectItem -> {

            SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;

            String columnName = selectExpressionItem.toString();

            if (columnName.indexOf(".")>0){

                String alias = columnName.split("[.]")[0];

                String table = aliasMap.get(alias);

                if ( table != null){
                    columnName = table+"."+columnName.split("[.]")[1];
                }

            }

            result.addField(columnName);

        });
    }

    private String cleanQuery(String query) {
        return query.replaceAll("(?i)use sakila;", "")
                .trim()
                .replaceAll("[ ]{2,}", " ");
    }



}
