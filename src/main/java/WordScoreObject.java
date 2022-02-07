//package com.example.idea;

public class WordScoreObject {
    private String word;
    private Double score;
    private Integer rank;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public WordScoreObject(String word, Double score) {
        this.word = word;
        this.score = score;
        this.rank = -1;
    }

    @Override
    public String toString() {
        int decimalPlaces = 4;
        return "{'"+ word + "'," + String.format("score=%."+decimalPlaces+"f", score) + ",rank="+rank+'}';
    }
}
