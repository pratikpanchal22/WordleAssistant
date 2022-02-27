package com.pratikpanchal.wordle.wordpredictor;

import com.pratikpanchal.wordle.tools.WordImporter;
import org.junit.jupiter.api.Test;

import java.util.*;

public class ViableComputeEngineTest {

    @Test
    public void viableComputeEngineTest(){
        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();
        List<String> words = wi.importWords(wordFile);

        Advisor advisor = new Advisor(words);
        List<Character> priorityCharList = new ArrayList<>(Arrays.asList('a','b','c','d','e','f','g','h','i'));
        ViableComputeEngine viableComputeEngine = new ViableComputeEngine(
                advisor.getCharPercentage(),
                priorityCharList,
                words);

        System.out.println(viableComputeEngine.getViableStringObjectsList());
    }
}
