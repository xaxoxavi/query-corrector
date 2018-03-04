package com.esliceu.extractor;

/**
 * Created by xavi on 4/03/18.
 */
public class Query {

    private String sentence;

    public Query(String sentence) {
        setSentence(sentence);
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
}
