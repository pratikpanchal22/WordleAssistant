package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class WordScoreObject {
    private String word;
    private Double score;
    private Integer rank;
    private final Double positionalScore;
    private Integer uniqueCharacterCount;
    private Integer repeatCharacterCount;

    public WordScoreObject(String word, Double score, Double positionalScore) {
        this.word = word;
        this.score = score;
        this.positionalScore = positionalScore;
        this.rank = -1;
        computeCharMetrics();
    }

    public WordScoreObject(String word) {
        this.word = word;
        this.score = Double.valueOf(100);
        this.positionalScore = Double.valueOf(100);
        this.rank = 1;
        computeCharMetrics();
    }

    public Integer getUniqueCharacterCount() {
        return uniqueCharacterCount;
    }

    public Integer getRepeatCharacterCount() {
        return repeatCharacterCount;
    }

    public Optional<String> getWord() {
        return Optional.ofNullable(word);
    }

    public void setWord(String word) {
        this.word = word;
        computeCharMetrics();
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

    private void computeCharMetrics() {

        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < this.word.length(); i++) {
            char c = word.charAt(i);
            map.put(c, map.getOrDefault(c, 0) + 1);
        }

        int rcc = 0;
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if (entry.getValue() > 1) {
                ++rcc;
            }
        }

        this.uniqueCharacterCount = map.size();
        this.repeatCharacterCount = rcc;
    }

    @Override
    public String toString() {
        int decimalPlaces = 4;
        return "{'" + word + "'," +
                String.format("score=%." + decimalPlaces + "f", score) + "'," +
                String.format("posScore=%." + decimalPlaces + "f", positionalScore) +
                " ,ucc=" + uniqueCharacterCount +
                " ,rcc=" + repeatCharacterCount +
                ",rank=" + rank + '}' + '\n';
    }

    public String getUniqueIdString() {
        return this.uniqueCharacterCount + ":"
                + this.repeatCharacterCount + ":"
                + this.positionalScore + ":"
                + this.score;
    }
}
