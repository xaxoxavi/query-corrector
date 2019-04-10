package com.esliceu.report;


import com.esliceu.ResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by xavi on 5/03/18.
 */
public class SimpleQueryReport implements QueryReport {

    private final Logger log = LoggerFactory.getLogger(SimpleQueryReport.class);

    private List<QueryReportStudent> students = new ArrayList<>();
    private Integer qualification;


    @Override
    public void doReport(){

        for (QueryReportStudent student : students) {

            log.info(student.getStudent());

            int OK = 0, ERROR = 0;

            for (QueryReportLine queryReportLine : student.getReportLines()) {

                switch (queryReportLine.getStatus()) {
                    case EXECUTE_ERROR:
                    case PARSE_ERROR:
                        ERROR++;
                        break;
                    case PARSE_OK:
                        log.info("-------------------------------------" + queryReportLine.getIndex() + "-------------------------------------");
                        OK++;
                }

            }


            log.info("-- SUMMARY:  --OK -> " + OK + "--- ERROR -> " + ERROR + "------------------------------------------");
            log.info(" ");

        }
    }

    @Override
    public void setIndex(Integer index) {
        actualReportLine().setIndex(index);
    }

    @Override
    public void addStudent(String studentId) {
        QueryReportStudent queryReportStudent = new QueryReportStudent();
        queryReportStudent.setStudent(studentId);

        students.add(queryReportStudent);
    }

    @Override
    public void setStatus(ResultStatus status) {
        actualReportLine().setStatus(status);
    }

    @Override
    public void setQuery(String query) {
        actualReportLine().setQuery(query);
    }

    @Override
    public void setExecuteError(ResultStatus status, Integer teacherRows, Integer studentRows, String errorMessage) {
        setStatus(status);
        setStudentRows(studentRows);
        setTeacherRows(teacherRows);
        setErrorMessage(errorMessage);
    }


    public void setStudentRows(Integer studentRows) {
        actualReportLine().setStudentRows(studentRows);
    }


    public void setTeacherRows(Integer teacherRows) {
        actualReportLine().setTeacherRows(teacherRows);
    }


    @Override
    public void setErrorMessage(String errorMessage) {
        actualReportLine().setErrorMessage(errorMessage);
    }

    @Override
    public void setStudentTables(Set<String> studentTables) {
        actualReportLine().setStudentTables(studentTables);
    }

    @Override
    public void setTeacherTables(Set<String> teacherTables) {
        actualReportLine().setTeacherTables(teacherTables);
    }


    @Override
    public void prepareNewResult() {
       lastStudent().getReportLines().add(new QueryReportLine());

    }

    @Override
    public void setTeacherFields(Set<String> fields) {

    }

    @Override
    public void setStudentFields(Set<String> fields) {

    }


    public Set<String> getTeacherFields() {
        return null;
    }


    public Set<String> getStudentFields() {
        return null;
    }

    private QueryReportStudent lastStudent(){
        return this.students.get(students.size()-1);

    }

    private QueryReportLine actualReportLine(){
        return lastStudent().getReportLines().get(lastStudent().getReportLines().size()-1);
    }
}
