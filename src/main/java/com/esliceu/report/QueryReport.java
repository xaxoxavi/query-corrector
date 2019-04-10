package com.esliceu.report;

import com.esliceu.ResultStatus;

import java.util.Set;

/**
 * Created by xavi on 5/03/18.
 */
public interface QueryReport {
    void doReport();

    void setIndex(Integer index);

    void addStudent(String studentId);

    void setStatus(ResultStatus status);

    void setQuery(String query);


    void setExecuteError(ResultStatus executeError, Integer teacherRows, Integer studentRows, String errorMessage);

    void setErrorMessage(String errorMessage);


    void setStudentTables(Set<String> studentTables);

    void setTeacherTables(Set<String> teacherTables);




    void prepareNewResult();

    void setTeacherFields(Set<String> fields);

    void setStudentFields(Set<String> fields);

}
