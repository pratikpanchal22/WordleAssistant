package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;

import java.util.HashMap;
import java.util.Map;

public class WordScoreObject {
    private String word;
    private Double score;
    private Integer rank;
    private Double positionalScore;

    public Integer getUniqueCharacterCount() {
        return uniqueCharacterCount;
    }

    private Integer uniqueCharacterCount;

    public Integer getRepeatCharacterCount() {
        return repeatCharacterCount;
    }

    private Integer repeatCharacterCount;

    public String getWord() {
        return word;
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

    public WordScoreObject(String word, Double score, Double positionalScore) {
        this.word = word;
        this.score = score;
        this.positionalScore = positionalScore;
        this.rank = -1;
        computeCharMetrics();
    }

    private void computeCharMetrics(){

        Map<Character,Integer> map = new HashMap<>();
        for(int i=0; i<this.word.length(); i++){
            char c = word.charAt(i);
            map.put(c, map.getOrDefault(c,0)+1);
        }

        int rcc=0;
        for(Map.Entry<Character,Integer> entry : map.entrySet()){
            if(entry.getValue()>1){
                ++rcc;
            }
        }

        this.uniqueCharacterCount=map.size();
        this.repeatCharacterCount=rcc;
    }

    @Override
    public String toString() {
        int decimalPlaces = 4;
        return "{'"+ word + "'," +
                String.format("score=%."+decimalPlaces+"f", score) + "'," +
                String.format("posScore=%."+decimalPlaces+"f", positionalScore) +
                " ,ucc="+uniqueCharacterCount +
                " ,rcc="+repeatCharacterCount +
                ",rank="+rank+'}'+'\n';
    }
}
