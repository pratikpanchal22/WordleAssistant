package com.pratikpanchal.wordle.responsefactory;

import java.util.Set;

public class AsyncWordleControllerResponseDataFactory {

    /**
     * computational output for the next best guess (the word)
     */
    private String suggestedWord;
    public String getSuggestedWord() {
        return suggestedWord;
    }


    /**
     * Solution Description: There are {n} potential solutions.
     */
    public static final String SSD1 = "<center><h3>There ";
    public static final String SSD1A = "is only 1 possible solution. This might be the soultion to your Wordle.</h3></center>";
    public static final String SSD2A = "are ";
    public static final String SSD2B = " potential solutions.</h3></center>";
    private String solutionSetDescription;
    public void setSolutionSetDescription(int solutionSetSize) {
        if(solutionSetSize==1){
            this.solutionSetDescription = SSD1+SSD1A;
        }
        else {
            this.solutionSetDescription = SSD1+SSD2A+solutionSetSize+SSD2B;
        }

    }
    public String getSolutionSetDescription() {
        return solutionSetDescription;
    }


    /**
     * Algorithm: Signal Guided Elimination / Solution Space Pruning
     *
     * SGE: This uses all the given hints to eliminate all such candidates that don't fit
     * the conditions. Out of the potential solutions, the with highest positional-score
     * fitness is chosen as the next best guess.
     *
     * SSP: This uses all the characters that are positionally unlocked in the entire
     * solution space (computed by SGE algorithm) to form a set of viable candidates.
     * Out of these, the one with the highest fitness is chosen as the next best guess.
     * Hence, this word may or may not be part of the solution set.
     */
    public enum ALGORITHM_TYPE {SSP, SGE}
    public static final String AD1 = "<b>Algorithm: ";
    public static final String AD_SGE1 = "Signal Guided Elimination</b><br>";
    public static final String AD_SSP1 = "Solution Space Pruning</b><br>";
    public static final String AD_SGE2 = "<br>This uses all the given hints to eliminate all such candidates that don't fit the conditions.<br><br>Out of these potential solutions, the one with the highest positional-score fitness is chosen as the next-best guess.<br>";
    public static final String AD_SSP2 = "<br>This uses all the characters that are positionally unlocked in the entire solution space (computed by SGE algorithm) to form a set of viable candidates.<br><br>Out of these, the one with the highest fitness is chosen as the next-best guess.<br>";
    public static final String AD_SSP3 = "Hence, this word <b>may or may not be part</b> of the solution set.";
    private String algorithmDescription;
    public void setAlgorithmDescription(ALGORITHM_TYPE algorithmType) {
        switch (algorithmType){
            case SSP -> this.algorithmDescription= AD1 + AD_SSP1 + AD_SSP2 + AD_SSP3;
            case SGE -> this.algorithmDescription= AD1 + AD_SGE1 + AD_SGE2;
        }
    }
    public String getAlgorithmDescription() {
        return algorithmDescription;
    }


    /**
     * The next best guess is {suggestedWord} and it has been autopopulated above. Use this word in your Wordle app
     * and add all the color hints provided by it by choosing the corresponding color above.
     * If your Wordle app dictionary does not contain this word, click '↺' next to it to recompute.
     */
    public static final String NBGD1 = "<b>The next best guess is '";
    public static final String NBGD2 = "'</b> and it has been auto-populated above.<br><br>Use this word in your Wordle app and add all the color hints provided by it by choosing the corresponding color above.<br>";
    public static final String NBGD3 = "<br><b>If your Wordle app dictionary does not contain this word, click '↺' next to it to recompute.</b>";
    private String nextBestGuessDescription;
    public void setNextBestGuessDescription(String nextBestGuess) {
        this.nextBestGuessDescription = NBGD1 + nextBestGuess + NBGD2 + NBGD3;
        this.suggestedWord = nextBestGuess;
    }
    public String getNextBestGuessDescription() {
        return nextBestGuessDescription;
    }


    /**
     * Data Set: There are total {n} words from which the guesses are selected.
     * So far, you have marked the following {m} words to be not part of your Wordle App dictionary:
     * Excluded words: {List of exclusions}
     *
     * These words will be excluded from the computational models.
     */
    public static final String DSD1 = "Data Set: There are total <b>";
    public static final String DSD2 = "</b> words from which the guesses are selected.<br>";
    public static final String DSD2A = "So far, you have marked the following words to be not part of your Wordle App dictionary:<br>";
    public static final String DSD2B = "<br>Excluded words: ";
    public static final String DSD2C = "<br><br>These words will be excluded from the computational models.";
    public static final String DSD3A = "So far, you haven't marked any words as excluded.";
    private String dataSetDescription;
    public void setDataSetDescription(int dataSetSize, Set<String> excludedWords) {
        this.dataSetDescription = DSD1+dataSetSize+DSD2;
        if(excludedWords.size()>0){
            this.dataSetDescription+= DSD2A + DSD2B + excludedWords.toString() + DSD2C;
        }
        else {
            this.dataSetDescription+= DSD3A;
        }
    }
    public String getDataSetDescription() {
        return dataSetDescription;
    }
}
