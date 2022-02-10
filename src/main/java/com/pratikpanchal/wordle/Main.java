package com.pratikpanchal.wordle;//package com.example.idea;

import com.pratikpanchal.wordle.hintprovider.HintProvider;
import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        List<String> words = wi.importWords(wordFile);

        //Hint provider
        HintProvider hp = new HintProvider("arosa");
        System.out.println(hp.provideHint("onion"));
        System.out.println(hp.provideHint("quilt"));
        System.out.println(hp.provideHint("weary"));
        System.out.println(hp.provideHint("axxae"));
        System.out.println(hp.provideHint("arose"));


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
        TrieNode root = Trie.addWordsToTrie(words);

        /*
        //Solve automatically for all words
        HashMap<Integer,Integer> solutionNumberOfTrialsToFreq = new HashMap<>();
        int wordNumber=0;
        for(String word : words){
            ++wordNumber;
            String trace = ":::"+wordNumber+" Secret word: "+word+"...::";
            HintProvider hintProvider = new HintProvider(word);
            InputGrid ig = new InputGrid();
            String hint = "BBBBB";
            int trial=0;
            while(!hint.equals("GGGGG")){
                ++trial;

                ComputationalInputs ci = new ComputationalInputs(ig);
                List<String> solutions = new ComputationalEngine().compute(root, ci);
                //System.out.println("SOLUTIONS:"+solutions);
                String predictedWord = advisor.suggestASolution(solutions);
                //String predictedWord = new Advisor(solutions).suggestASolution(solutions);

                if(predictedWord==null){
                    System.out.println("No solution found. Hint="+hint);
                    trace+="...TERMINATED...";
                    break;
                }

                hint = hintProvider.provideHint(predictedWord);
                ig.addRow(predictedWord, hint);
                trace+=">>t="+trial+" ["+solutions.size()+"]"+predictedWord+"["+hint+"] ";
            }
            System.out.println(trace);
            solutionNumberOfTrialsToFreq.put(trial, solutionNumberOfTrialsToFreq.getOrDefault(trial,0)+1);
        }
        System.out.println("trial distribution");
        System.out.println(solutionNumberOfTrialsToFreq.toString());
        */


        //Solve manually
        //Create new com.pratikpanchal.wordle.wordpredictor.InputGrid
        InputGrid inputGrid = new InputGrid();
        inputGrid.addRow("arose","YBBBB");
        inputGrid.addRow("latin","BGBBB");
        inputGrid.addRow("gaudy","BGBBG");
        inputGrid.addRow("campy","BGBGG");
        inputGrid.addRow("pappy","BGGGG");

        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);

        List<String> solution = new ComputationalEngine().compute(root, computationalInputs);

        System.out.println("\nRanked solution with scores: Total size:"+solution.size());
        System.out.println(advisor.getAllWordScoreObjects(solution).toString());

        List<WordScoreObject> wordScoreObjects = advisor.getWordScoreObjectsWithoutRepetitiveCharacters(solution);
        System.out.println("\nRanked solution with scores (unique characters only): Size:"+ wordScoreObjects.size());
        System.out.println(wordScoreObjects.toString());
    }
}
