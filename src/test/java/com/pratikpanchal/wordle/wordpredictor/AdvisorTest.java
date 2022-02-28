package com.pratikpanchal.wordle.wordpredictor;

import com.pratikpanchal.wordle.tools.WordImporter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class AdvisorTest {

    @Test
    public void advisorTest1(){
        System.out.println("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        //Get the super set of all possible words
        List<String> words = wi.importWords(wordFile);

        Advisor advisor = new Advisor(words);
        System.out.println(advisor.getAllWordScoreObjects(words, Optional.of(0)).toString());
    }
}
