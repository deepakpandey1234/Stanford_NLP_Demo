package com.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StatementResult {
    private String validLinks;
    private String searchKeyword;
    private List<String> sentiment = new ArrayList<>();
    private Map<String, List<String>> wordAndPos = new LinkedHashMap<>();
    private List<String> semanticGraph;
    private String highestPolarityOfPage;
    private String relevantText;

    public String getRelevantText() {
        return relevantText;
    }

    public void setRelevantText(String relevantText) {
        this.relevantText = relevantText;
    }

    public String getHighestPolarityOfPage() {
        return highestPolarityOfPage;
    }

    public void setHighestPolarityOfPage(String highestPolarityOfPage) {
        this.highestPolarityOfPage = highestPolarityOfPage;
    }

    public String getValidLinks() {
        return validLinks;
    }

    public void setValidLinks(String validLinks) {
        this.validLinks = validLinks;
    }

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public List<String> getSentiment() {
        return sentiment;
    }

    public void setSentiment(List<String> sentiment) {
        this.sentiment = sentiment;
    }

    public Map<String, List<String>> getWordAndPos() {
        return wordAndPos;
    }

    public void setWordAndPos(Map<String, List<String>> wordAndPos) {
        this.wordAndPos = wordAndPos;
    }

    public List<String> getSemanticGraph() {
        return semanticGraph;
    }

    public void setSemanticGraph(List<String> semanticGraph) {
        this.semanticGraph = semanticGraph;
    }

    @Override
    public String toString() {
        return "StatementResult{\n" +
                "\n\nvalidLinks='" + validLinks + '\'' +
                "\n\nRelevant Content=' : " + relevantText + '\'' +
                ",\n\n searchKeyword='" + searchKeyword + '\'' +
                ", \n\nsentiment=" + sentiment +
                ", \n\nwordAndPos=" + wordAndPos +
                ", \n\nsemanticGraph=" + semanticGraph +
                ", \n\nhighestPolarityOfPage='" + highestPolarityOfPage + '\'' +
                "}\n\n\n";
    }
}
