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
        System.out.println("Post body: "+ requestMap.toString());

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
        System.out.println(solutions.toString());

        if(computationalInputs.getNumberOfPositionalLocks()>=2 && trial<=5){
            List<Character> priorityChars = computationalInputs.getStrictListOfPriorityCharactersFromStrings(solutions);
            ViableComputeEngine viableComputeEngine = new ViableComputeEngine(
                    advisor.getCharPercentage(),
                    priorityChars,
                    words);
            List<ViableComputeEngine.ViableStringObject> vsoList = viableComputeEngine.getViableStringObjectsList();
            System.out.println(vsoList.toString());

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

    private void parseExcludedWords(String spaceSeparatedWords, Set<String> excludedWords) {
        int left=0;
        for(int i=1; i<spaceSeparatedWords.length(); i++){
            if(spaceSeparatedWords.charAt(i)==' '){
                excludedWords.add(spaceSeparatedWords.substring(left,i).toLowerCase(Locale.ROOT));
                left=i+1;
            }
        }
        excludedWords.add(spaceSeparatedWords.substring(left));
    }
}
