//package com.example.idea;

public class TrieNode {
    char c;
    boolean endsHere;
    int numberOfBranches;
    TrieNode[] next;

    public TrieNode() {
        this.endsHere=false;
        this.numberOfBranches=0;
        next = new TrieNode[26];
    }

    public TrieNode(char c) {
        this();
        this.c = c;
    }
}