package com.pratikpanchal.wordle.controller;

import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class AsyncWordleController {

    @RequestMapping(
            value = "/solve",
            method = POST
    )
    @ResponseBody
    public List<WordScoreObject> solve(@RequestBody Map<String,String> requestMap){
        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();
        List<String> words = wi.importWords(wordFile);
        TrieNode root = Trie.addWordsToTrie(words);

        System.out.println("Post body: "+ requestMap.toString());

        InputGrid inputGrid = new InputGrid();
        for(String key : requestMap.keySet()){
            inputGrid.addRow(key, requestMap.get(key));
        }

        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);
        List<String> solutions = new ComputationalEngine().compute(root, computationalInputs);
        Advisor advisor = new Advisor(solutions);
        int trial = requestMap.size();
        String predictedWord = advisor.suggestASolution(solutions, Optional.of(trial));

        if(computationalInputs.getNumberOfPositionalLocks()>=2 && trial<=5){
            List<Character> priorityChars = computationalInputs.getStrictListOfPriorityCharactersFromStrings(solutions);
            ViableComputeEngine viableComputeEngine = new ViableComputeEngine(
                    advisor.getCharPercentage(),
                    priorityChars,
                    words);
            List<ViableComputeEngine.ViableStringObject> vsoList = viableComputeEngine.getViableStringObjectsList();

            if (vsoList.size() > 0 && vsoList.get(0).getUpcc() >= 2) {
                predictedWord = vsoList.get(0).getWord();
            }
        }

        if(predictedWord==null){
            return null;
        }

        WordScoreObject wordScoreObject = new WordScoreObject(predictedWord);
        return new ArrayList<WordScoreObject>(Arrays.asList(wordScoreObject));
    }
}
