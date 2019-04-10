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
public class LoggerQueryReport implements QueryReport {

    private final Logger log = LoggerFactory.getLogger(LoggerQueryReport.class);

    private List<QueryReportStudent> students = new ArrayList<>();



    @Override
    public void doReport(){

        for (QueryReportStudent student : students) {

            log.info(student.getStudent());

            int OK = 0, ERROR = 0;

            for (QueryReportLine queryReportLine : student.getReportLines()) {



                log.info("-------------------------------------" + queryReportLine.getIndex() + "-------------------------------------");


                switch (queryReportLine.getStatus()) {
                    case IGNORED:
                        log.info(" QUERY IGNORED!!!");
                        break;
                    case EXECUTE_ERROR:
                        ERROR++;
                        log.error("QUERY: " + queryReportLine.getQuery());
                        log.error("TEACHER rows --> " + queryReportLine.getTeacherRows() +
                                " | STUDENT rows --> " + queryReportLine.getStudentRows());
                        log.error(queryReportLine.getErrorMessage());
                        break;
                    case PARSE_ERROR:
                        ERROR++;
                        log.warn("PARSE ERROR! Review query");
                        log.warn(queryReportLine.getErrorMessage());
                        break;
                    case DIF_TABLES:
                        ERROR++;
                        log.error("****** DIFERENT TABLES ********");
                        log.error("TEACHER-> " + queryReportLine.getTeacherTables().size());
                        queryReportLine.getTeacherTables().stream().forEach(t -> log.error(t));

                        log.error("STUDENT-> " + queryReportLine.getStudentTables().size());
                        queryReportLine.getStudentTables().stream().forEach(t -> log.error(t));
                        break;
                    case PARSE_OK:
                        OK++;
                        log.info(" QUERY OK!!!");

                        if (queryReportLine.getTeacherFields().size() != queryReportLine.getStudentFields().size()) {
                            log.warn("******** DIFERENT FIELDS ************ ");
                            log.error("TEACHER-> " + queryReportLine.getTeacherFields().size());
                            queryReportLine.getTeacherFields().stream().forEach(t -> log.error(t));

                            log.error("STUDENT-> " + queryReportLine.getStudentFields().size());
                            queryReportLine.getStudentFields().stream().forEach(t -> log.error(t));
                        }

                }

            }


            log.info("-- SUMMARY  ----------------------------------------------------------------------------");
            log.info("--- OK -> " + OK + "---- ERROR -> " + ERROR + "-----------------------------------------");
            log.info("----------------------------------------------------------------------------------------");
            log.info(" NOTA FINAL " + ((OK+ERROR)>0 ? OK * 100 / (OK + ERROR) : "0"));
            log.info("----------------------------------------------------------------------------------------");
            log.info("----------------------------------------------------------------------------------------");

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
        actualReportLine().setTeacherFields(fields);
    }

    @Override
    public void setStudentFields(Set<String> fields) {
       actualReportLine().setStudentFields(fields);
    }


    private QueryReportStudent lastStudent(){
        return this.students.get(students.size()-1);

    }

    private QueryReportLine actualReportLine(){
        return lastStudent().getReportLines().get(lastStudent().getReportLines().size()-1);
    }


}
