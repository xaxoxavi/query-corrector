package com.esliceu;

import com.esliceu.extractor.Query;
import com.esliceu.parser.ColumnsAliasesVisitor;
import com.esliceu.parser.TableNamesAndAliasFinder;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by xavi on 26/02/18.
 */
@Component
public class QueryAnalyzer {

    @Autowired
    private DataSource dataSource;

    @Value("${analyzer.ignore-queries}")
    private String ignoreQueries;


    public List<Result> analyzeQueries(List<Query> queries)  {

        Connection connection = null;

        List<Result> results = new ArrayList<>();


        for (Query query : queries){

            Result result = new Result();
            Statement statement = null;

            if (ignoreQueries.contains(query.getOrder().toString())){
                result.setStatus(ResultStatus.IGNORED);
                result.setOrder(query.getOrder());
                results.add(result);

            } else {


                ColumnsAliasesVisitor columnsAliasesVisitor = null;

                try {
                    query.setSentence(cleanQuery(query.getSentence()));
                    result.setQuery(query.getSentence());

                    statement = CCJSqlParserUtil.parse(query.getSentence());


                    Select selectStatement = (Select) statement;

                    TableNamesAndAliasFinder tablesNamesFinder = new TableNamesAndAliasFinder();
                    List<String> tables = tablesNamesFinder.getTableList(selectStatement);
                    tables.forEach(result::addTable);

                    columnsAliasesVisitor = new ColumnsAliasesVisitor();
                    selectStatement.getSelectBody().accept(columnsAliasesVisitor);



                    if (selectStatement.getSelectBody() instanceof PlainSelect) {

                        PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();

                        extractFields(plainSelect, result, tablesNamesFinder.getAlias());

                    } else if (selectStatement.getSelectBody() instanceof SetOperationList) {

                        SetOperationList setOperationList = (SetOperationList) selectStatement.getSelectBody();

                        setOperationList.getPlainSelects().stream().forEach(ps -> extractFields(ps, result, tablesNamesFinder.getAlias()));

                    }


                } catch (JSQLParserException e) {
                    e.printStackTrace();
                    result.setStatus(ResultStatus.PARSE_ERROR);
                    result.setParseErrorMessage(e.getCause().getMessage().split("\n")[0]);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    result.setStatus(ResultStatus.PARSE_ERROR);
                    result.setParseErrorMessage(ex.getMessage());
                }

                try {
                    connection = dataSource.getConnection();

                    connection.prepareStatement("SET sql_mode='';").execute();


                    PreparedStatement preparedStatement = connection.prepareStatement(query.getSentence());

                    ResultSet rs = preparedStatement.executeQuery();

                    int count = 0;

                    Set<String> resultRows = new HashSet<>();

                    Set<String> columNames = new TreeSet<>();

                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        columNames.add(rs.getMetaData().getColumnName(i));
                    }

                    StringBuilder row = new StringBuilder();
                    while (rs.next()) {
                        row.setLength(0);

                        ColumnsAliasesVisitor finalColumnsAliasesVisitor = columnsAliasesVisitor;

                        columNames.stream().forEach(column -> {
                            try {

                                String alias = finalColumnsAliasesVisitor.getAliases().get(column);

                                row.append(rs.getObject(alias != null ? alias : column));
                                row.append("#");
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        });

                        resultRows.add(row.toString());
                        count++;

                    }
                    result.setRowNumber(count);
                    result.setResultRows(resultRows);

                } catch (Throwable ex) {
                    ex.printStackTrace();
                    result.setErrorMessage(ex.getMessage() + " " + ex.getCause());

                } finally {
                    results.add(result);
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException ignored) {

                        }
                    }
                }
            }

        }



        return results;



    }

    private void extractFields(PlainSelect plainSelect, Result result, Map<String,String> aliasMap) {
        plainSelect.getSelectItems().stream().forEach(selectItem -> {

            if (selectItem instanceof SelectExpressionItem) {

                SelectExpressionItem selectExpressionItem = (SelectExpressionItem) selectItem;

                String columnName = selectExpressionItem.toString();

                if (columnName.indexOf(".") > 0) {

                    String alias = columnName.split("[.]")[0];

                    String table = aliasMap.get(alias);

                    if (table != null) {
                        columnName = table + "." + columnName.split("[.]")[1];
                    }

                }

                result.addField(columnName);
            }

        });
    }

    private String cleanQuery(String query) {
        return query.replaceAll("(?i)use sakila;", "")
                .trim()
                .replaceAll("[ ]{2,}", " ");
    }



}
