package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;

public class WordScoreObject {
    private String word;
    private Double score;
    private Integer rank;
    private Double positionalScore;

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

    public Double getPositionalScore() {
        return positionalScore;
    }

    public WordScoreObject(String word, Double score, Double positionalScore) {
        this.word = word;
        this.score = score;
        this.positionalScore = positionalScore;
        this.rank = -1;
    }

    @Override
    public String toString() {
        int decimalPlaces = 4;
        return "{'"+ word + "'," +
                String.format("score=%."+decimalPlaces+"f", score) + "'," +
                String.format("posScore=%."+decimalPlaces+"f", positionalScore) +
                ",rank="+rank+'}'+'\n';
    }
}
