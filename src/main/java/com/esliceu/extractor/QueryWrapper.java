package com.esliceu.extractor;

import java.util.List;

/**
 * Created by xavi on 4/03/18.
 */
public class QueryWrapper {

    private List<Query> queries;
    private String student;

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }
}
