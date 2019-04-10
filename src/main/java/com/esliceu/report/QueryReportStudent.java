package com.esliceu.report;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xavi on 11/03/18.
 */
public class QueryReportStudent {

    private List<QueryReportLine> reportLines = new ArrayList<>();
    private String student;
    private Integer qualification;

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public Integer getQualification() {
        return qualification;
    }

    public void setQualification(Integer qualification) {
        this.qualification = qualification;
    }

    public List<QueryReportLine> getReportLines() {
        return reportLines;
    }
}
