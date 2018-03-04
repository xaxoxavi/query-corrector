package com.esliceu;

import com.esliceu.extractor.Query;
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


    public List<Result> correctQueries(List<Query> queries) throws SQLException {


        queries.stream().forEach(query -> System.out.println(query));

        Connection connection = dataSource.getConnection();

        connection.prepareStatement("SET sql_mode='';").execute();

        List<Result> results = new ArrayList<>();

        for (Query query : queries){

            System.out.println("#####" + query);

            Result result = new Result();
            Statement statement = null;

            try{
                query.setSentence(cleanQuery(query.getSentence()));
                result.setQuery(query.getSentence());

                statement = CCJSqlParserUtil.parse(query.getSentence());


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

                PreparedStatement preparedStatement = connection.prepareStatement(query.getSentence());

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
