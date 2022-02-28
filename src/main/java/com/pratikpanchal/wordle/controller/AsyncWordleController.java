package com.pratikpanchal.wordle.controller;

import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        List<String> solution = new ComputationalEngine().compute(root, computationalInputs);

        Advisor advisor = new Advisor(solution);
        List<WordScoreObject> wordScoreObjects=advisor.getAllWordScoreObjects(solution, Optional.of(0));

        return wordScoreObjects;
    }
}
