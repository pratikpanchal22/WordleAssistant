//package com.example.idea;

public class WordScoreObject {
    String word;
    Double score;

    public WordScoreObject(String word, Double score) {
        this.word = word;
        this.score = score;
    }

    @Override
    public String toString() {
        return "{" +
                "word='" + word + '\'' +
                ", score=" + score +
                '}';
    }
}
