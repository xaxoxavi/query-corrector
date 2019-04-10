package com.esliceu;

import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xavi on 26/02/18.
 */
public class Result {

    private Integer rowNumber = 0;
    private Set<String> tables = new HashSet<>();
    private Set<String> fields = new HashSet<>();
    private String query;

    private String errorMessage;
    private ResultStatus status;
    private String parseErrorMessage;
    private Set<String> resultRows;
    private int order;

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void addTable(String table) {
        this.tables.add(table);
    }


    public Set<String> getTables() {
        return tables;
    }

    public void addField(String field) {
        if (field != null && field.length() > 0) this.fields.add(field);
    }

    public Set<String> getFields() {
        return fields;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }


    public ResultStatus getStatus() {
        return status;
    }

    public String getParseErrorMessage() {
        return parseErrorMessage;
    }

    public void setParseErrorMessage(String parseErrorMessage) {
        this.parseErrorMessage = parseErrorMessage;
    }



    public void setResultRows(Set<String> resultRows) {
        this.resultRows = resultRows;
    }

    public Set<String> getResultRows() {
        return resultRows;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
