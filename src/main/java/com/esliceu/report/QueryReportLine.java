package com.esliceu.report;

import com.esliceu.ResultStatus;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by xavi on 11/03/18.
 */
public class QueryReportLine {

    private Integer index;

    private ResultStatus status;
    private String query;

    private Integer studentRows;
    private Integer teacherRows;

    private Set<String> studentTables = new HashSet<>();
    private Set<String> teacherTables= new HashSet<>();

    private Set<String> teacherFields  = new HashSet<>();;
    private Set<String> studentFields  = new HashSet<>();;


    private String errorMessage;


    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }


    public ResultStatus getStatus() {
        return status;
    }

    public void setStatus(ResultStatus status) {
        this.status = status;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getStudentRows() {
        return studentRows;
    }

    public void setStudentRows(Integer studentRows) {
        this.studentRows = studentRows;
    }

    public Integer getTeacherRows() {
        return teacherRows;
    }

    public void setTeacherRows(Integer teacherRows) {
        this.teacherRows = teacherRows;
    }

    public Set<String> getStudentTables() {
        return studentTables;
    }

    public void setStudentTables(Set<String> studentTables) {
        this.studentTables = studentTables;
    }

    public Set<String> getTeacherTables() {
        return teacherTables;
    }

    public void setTeacherTables(Set<String> teacherTables) {
        this.teacherTables = teacherTables;
    }


    public void setTeacherFields(Set<String> fields) {
        this.teacherFields=fields;
    }


    public void setStudentFields(Set<String> fields) {
        this.studentFields = fields;
    }

    public Set<String> getTeacherFields() {
        return teacherFields;
    }


    public Set<String> getStudentFields() {
        return studentFields;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


}
