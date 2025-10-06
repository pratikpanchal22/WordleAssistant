package com.pratikpanchal.wordle.controller;

import com.pratikpanchal.wordle.responsefactory.AsyncWordleControllerResponseDataFactory;
import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class AsyncWordleController {

    private static final Logger log = LogManager.getLogger(AsyncWordleController.class);

    private static final String WORD_FILE_PATH = "src/main/resources/wordList_5Letter.txt";

    /**
     * Cached full word list and Trie for faster request handling
     */
    private List<String> cachedWords;
    private TrieNode cachedTrieRoot;

    /**
     * Load words and trie at startup
     */
    @PostConstruct
    public void init() {
        log.info("Loading word list and building trie...");
        cachedWords = new WordImporter().importWords(WORD_FILE_PATH, Collections.emptySet());
        cachedTrieRoot = Trie.addWordsToTrie(cachedWords);
        log.info("Word list loaded ({} words). Trie built successfully.", cachedWords.size());
    }

    @PostMapping("/solve")
    public ResponseEntity<AsyncWordleControllerResponseDataFactory> solve(@RequestBody Map<String
            , String> requestMap) {
        if (requestMap == null || requestMap.isEmpty()) {
            log.warn("Empty request received");
        }

        log.info("Received request: {}", requestMap);

        // Handle exclusions
        Set<String> excludedWords = new HashSet<>();
        Optional.ofNullable(requestMap.remove("exclusions"))
                .ifPresent(exclusions -> parseExcludedWords(exclusions, excludedWords));

        // Build input grid
        InputGrid inputGrid = new InputGrid();
        requestMap.forEach(inputGrid::addRow);

        // Use cached word list and trie, filter excluded words
        List<String> words = new ArrayList<>(cachedWords);
        words.removeAll(excludedWords);

        TrieNode root = cachedTrieRoot; // Using cached trie (we can skip rebuilding)

        // Compute solutions
        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);
        List<String> solutions = new ComputationalEngine().compute(root, computationalInputs);

        Advisor advisor = new Advisor(solutions);
        int trial = requestMap.size();
        String predictedWord = advisor.suggestASolution(solutions, trial).orElse(null);

        // Initialize response
        AsyncWordleControllerResponseDataFactory response =
                new AsyncWordleControllerResponseDataFactory();
        response.setSolutionSetDescription(solutions.size());
        response.setAlgorithmDescription(AsyncWordleControllerResponseDataFactory.ALGORITHM_TYPE.SGE);
        response.setDataSetDescription(words.size(), excludedWords);

        // Optional: compute viable solution
        if (computationalInputs.getNumberOfPositionalLocks() >= 2 && trial <= 5 && solutions.size() > 2) {
            List<Character> priorityChars =
                    computationalInputs.getStrictListOfPriorityCharactersFromStrings(solutions);
            ViableComputeEngine viableComputeEngine =
                    new ViableComputeEngine(advisor.getCharPercentage(), priorityChars, words);
            List<ViableComputeEngine.ViableStringObject> vsoList =
                    viableComputeEngine.getViableStringObjectsList();

            if (!vsoList.isEmpty() && vsoList.get(0).getUpcc() >= 2) {
                predictedWord = vsoList.get(0).getWord();
                response.setAlgorithmDescription(AsyncWordleControllerResponseDataFactory.ALGORITHM_TYPE.SSP);
            }
        }

        response.setNextBestGuessDescription(predictedWord);
        log.info("Response: {}", response);

        return ResponseEntity.ok(response);
    }

    private void parseExcludedWords(String spaceSeparatedWords, Set<String> excludedWords) {
        if (spaceSeparatedWords == null || spaceSeparatedWords.isBlank()) return;

        String[] words = spaceSeparatedWords.trim().toLowerCase(Locale.ROOT).split("\\s+");
        Collections.addAll(excludedWords, words);
    }
}