//package com.example.idea;

import tools.WordImporter;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        List<String> words = wi.importWords(wordFile);

        //Advisor
        Advisor advisor = new Advisor(words);
        List<Character> l = advisor.getKMostUsedCharacters(26);
        System.out.println("\nList of words with atleast 2 repetitive chars:\n"+
                advisor.getWordsWithAtleastKCharacterFrequency(2).toString()+"\n");
        System.out.println("\nList of words with atleast 3 repetitive chars:\n"+
                advisor.getWordsWithAtleastKCharacterFrequency(3).toString()+"\n");
        System.out.println("\nList of words with atleast 4 repetitive chars:\n"+
                advisor.getWordsWithAtleastKCharacterFrequency(4).toString()+"\n");

        //Create Trie
        Node root = Trie.addWordsToTrie(words);

        //Create new InputGrid
        InputGrid inputGrid = new InputGrid();
        inputGrid.addRow("arose","BBYBB");
//        inputGrid.addRow("pilot","BYBGB");
//        inputGrid.addRow("crane","BGGBG");
//        inputGrid.addRow("drape","BGGBG");

        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);

        List<String> solution = new ComputationalEngine().compute(root, computationalInputs);

        System.out.println("\nRanked solution with scores: Total size:"+solution.size());
        System.out.println(advisor.getAllWordScoreObjects(solution).toString());

        List<WordScoreObject> wordScoreObjects = advisor.getWordScoreObjectsWithoutRepetitiveCharacters(solution);
        System.out.println("\nRanked solution with scores (unique characters only): Size:"+ wordScoreObjects.size());
        System.out.println(wordScoreObjects.toString());
    }
}
