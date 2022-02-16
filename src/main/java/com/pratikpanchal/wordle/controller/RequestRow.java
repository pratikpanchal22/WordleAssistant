package com.pratikpanchal.wordle.controller;

public class RequestRow {
    String word;
    String hint;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    @Override
    public String toString() {
        return "SolveRequestNode{" +
                "word='" + word + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }
}
