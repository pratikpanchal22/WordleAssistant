//package com.example.idea;

public class Node {
    char c;
    boolean endsHere;
    int numberOfBranches;
    Node[] next;

    public Node() {
        this.endsHere=false;
        this.numberOfBranches=0;
        next = new Node[26];
    }

    public Node(char c) {
        this();
        this.c = c;
    }
}