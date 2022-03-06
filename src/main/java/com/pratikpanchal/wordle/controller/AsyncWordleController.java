package com.pratikpanchal.wordle.controller;

import com.pratikpanchal.wordle.responsefactory.AsyncWordleControllerResponseDataFactory;
import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class AsyncWordleController {

    private final Logger log = LogManager.getLogger(this.getClass());

    @RequestMapping(
            value = "/solve",
            method = POST
    )
    @ResponseBody
    public AsyncWordleControllerResponseDataFactory solve(@RequestBody Map<String,String> requestMap){
        log.info("Post request body: "+ requestMap.toString());

        InputGrid inputGrid = new InputGrid();
        Set<String> excludedWords = new HashSet<>();
        if(requestMap.containsKey("exclusions")){
            parseExcludedWords(requestMap.get("exclusions"), excludedWords);
            requestMap.remove("exclusions");
        }

        for(String key : requestMap.keySet()){
            inputGrid.addRow(key, requestMap.get(key));
        }

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();
        List<String> words = wi.importWords(wordFile, excludedWords);
        TrieNode root = Trie.addWordsToTrie(words);

        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);
        List<String> solutions = new ComputationalEngine().compute(root, computationalInputs);
        Advisor advisor = new Advisor(solutions);
        int trial = requestMap.size();
        String predictedWord = advisor.suggestASolution(solutions, Optional.of(trial));

        //Initialize a response object
        AsyncWordleControllerResponseDataFactory response = new AsyncWordleControllerResponseDataFactory();
        response.setSolutionSetDescription(solutions.size());
        response.setAlgorithmDescription(AsyncWordleControllerResponseDataFactory.ALGORITHM_TYPE.SGE);
        response.setDataSetDescription(words.size(), excludedWords);

        if(computationalInputs.getNumberOfPositionalLocks()>=2 && trial<=5){
            List<Character> priorityChars = computationalInputs.getStrictListOfPriorityCharactersFromStrings(solutions);
            ViableComputeEngine viableComputeEngine = new ViableComputeEngine(
                    advisor.getCharPercentage(),
                    priorityChars,
                    words);
            List<ViableComputeEngine.ViableStringObject> vsoList = viableComputeEngine.getViableStringObjectsList();

            if (vsoList.size() > 0 && vsoList.get(0).getUpcc() >= 2) {
                predictedWord = vsoList.get(0).getWord();
                response.setAlgorithmDescription(AsyncWordleControllerResponseDataFactory.ALGORITHM_TYPE.SSP);
            }
        }

        response.setNextBestGuessDescription(predictedWord);
        log.info(response.toString());

        return response;
    }

    private void parseExcludedWords(String spaceSeparatedWords, Set<String> excludedWords) {
        int left=0;
        for(int i=1; i<spaceSeparatedWords.length(); i++){
            if(spaceSeparatedWords.charAt(i)==' '){
                excludedWords.add(spaceSeparatedWords.substring(left,i).trim().toLowerCase(Locale.ROOT));
                left=i+1;
            }
        }
        if(spaceSeparatedWords.length()>1){
            excludedWords.add(spaceSeparatedWords.substring(left).trim().toLowerCase(Locale.ROOT));
        }
    }
}
