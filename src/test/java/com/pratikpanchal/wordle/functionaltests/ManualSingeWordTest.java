package com.pratikpanchal.wordle.functionaltests;

import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class ManualSingeWordTest {

    @Test
    public void manualSingleWordTest(){

        System.out.println("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        //Get the super set of all possible words
        List<String> words = wi.importWords(wordFile);
        Advisor advisor;

        //Create Trie
        TrieNode root = Trie.addWordsToTrie(words);
        //Solve manually
        //Create new com.pratikpanchal.wordle.wordpredictor.InputGrid
        InputGrid inputGrid = new InputGrid();
        inputGrid.addRow("arose","BYBBY");
        inputGrid.addRow("liver","YBBGG");
//        inputGrid.addRow("gaudy","BGBBG");
//        inputGrid.addRow("campy","BGBGG");
//        inputGrid.addRow("pappy","BGGGG");

        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);

        List<String> solution = new ComputationalEngine().compute(root, computationalInputs);

        advisor = new Advisor(solution);

        System.out.println("\nRanked solution with scores: Total size:"+solution.size());
        System.out.println(advisor.getAllWordScoreObjects(solution, Optional.of(0)).toString());

        List<WordScoreObject> wordScoreObjects = advisor.getAllWordScoreObjects(solution, Optional.of(0));
        System.out.println("\nRanked solution with scores (unique characters only): Size:"+ wordScoreObjects.size());
        System.out.println(wordScoreObjects.toString());
    }
}
