package com.esliceu.extractor;

/**
 * Created by xavi on 4/03/18.
 */
public class Query {

    private String sentence;
    private Integer order;



    public Query(String sentence) {
        setSentence(sentence);
    }

    public Query(String sentence, Integer queryOrder) {
        this.order = queryOrder;
        this.sentence = sentence;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String toString(){
        return sentence;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
