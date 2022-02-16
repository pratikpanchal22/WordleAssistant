package com.pratikpanchal.wordle.controller;

import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class AsyncWordleController {

    @RequestMapping(
            value = "/solve",
            method = POST
            //headers = "Accept=application/json"
    )
    @ResponseBody
    public List<WordScoreObject> solve(@RequestBody List<RequestRow> requestRows){
        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();
        List<String> words = wi.importWords(wordFile);
        TrieNode root = Trie.addWordsToTrie(words);

        //System.out.printf("Request:"+w.toString() + " "+ h.toString());
        System.out.println("Post body: "+ requestRows.toString());

        InputGrid inputGrid = new InputGrid();
        for(RequestRow requestRow : requestRows){
            inputGrid.addRow(requestRow.word, requestRow.hint);
        }

        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);
        List<String> solution = new ComputationalEngine().compute(root, computationalInputs);

        Advisor advisor = new Advisor(solution);

        List<WordScoreObject> wordScoreObjects = advisor.getWordScoreObjectsWithoutRepetitiveCharacters(solution);
        if(wordScoreObjects.size()==0){
            wordScoreObjects=advisor.getAllWordScoreObjects(solution);
        }

        return wordScoreObjects;
    }
}
