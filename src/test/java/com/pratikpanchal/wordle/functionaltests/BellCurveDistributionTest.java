package com.pratikpanchal.wordle.functionaltests;

import com.pratikpanchal.wordle.hintprovider.HintProvider;
import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

public class BellCurveDistributionTest {

    @Test
    public void computeEntireSet(){
        System.out.println("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        List<String> words = wi.importWords(wordFile);

        //Advisor
        Advisor advisor = new Advisor(words);
        List<Character> l = advisor.getKMostUsedCharacters(26);
        System.out.println("\nList of words with atleast 2 repetitive chars:\n"+
                advisor.getWordsWithAtleastKCharacterFrequency(2).toString()+"\n");
        System.out.println("\nList of words with atleast 3 repetitive chars:\n"+
                advisor.getWordsWithAtleastKCharacterFrequency(3).toString()+"\n");
        System.out.println("\nList of words with atleast 4 repetitive chars:\n"+
                advisor.getWordsWithAtleastKCharacterFrequency(4).toString()+"\n");

        //Create Trie
        TrieNode root = Trie.addWordsToTrie(words);

        //Solve automatically for all words
        //map: Number of trials to solve -> frequency
        HashMap<Integer,Integer> solutionNumberOfTrialsToFreq = new HashMap<>();

        //map: number of trials to reach to n positionally-locked state -> frequency
        HashMap<Integer,Integer> posLocks4ToFreqMap = new HashMap<>();
        HashMap<Integer,Integer> posLocks3ToFreqMap = new HashMap<>();
        HashMap<Integer,Integer> posLocks2ToFreqMap = new HashMap<>();
        int wordNumber=0;
        for(String word : words){
            ++wordNumber;
            String trace = ":::"+wordNumber+" Secret word: "+word+"...::";
            String trace1 = "---";
            HintProvider hintProvider = new HintProvider(word);
            InputGrid ig = new InputGrid();
            String hint = "BBBBB";
            int trial=0;
            boolean captured=false,captured2=false,captured3=false;
            while(!hint.equals("GGGGG")){
                ++trial;

                ComputationalInputs ci = new ComputationalInputs(ig);
                List<String> solutions = new ComputationalEngine().compute(root, ci);
                //System.out.println("SOLUTIONS:"+solutions);
                String predictedWord = new Advisor(solutions).suggestASolution(solutions);
                //String predictedWord = new Advisor(solutions).suggestASolution(solutions);

                if(predictedWord==null){
                    System.out.println("No solution found. Hint="+hint);
                    trace+="...TERMINATED...";
                    break;
                }

                hint = hintProvider.provideHint(predictedWord);

                if(numberOfPositionalLocksInHint(hint)==2 & !captured2){
                    captured=true;
                    posLocks2ToFreqMap.put(trial, posLocks2ToFreqMap.getOrDefault(trial,0)+1);
                }
                else if(numberOfPositionalLocksInHint(hint)==4 & !captured){
                    captured=true;
                    posLocks4ToFreqMap.put(trial, posLocks4ToFreqMap.getOrDefault(trial,0)+1);
                }
                else if(numberOfPositionalLocksInHint(hint)==3 & !captured3){
                    captured=true;
                    posLocks3ToFreqMap.put(trial, posLocks3ToFreqMap.getOrDefault(trial,0)+1);
                }

                trace1+="t="+trial+" pLocks="+numberOfPositionalLocksInHint(hint)+" | ";

                ig.addRow(predictedWord, hint);
                trace+=">>t="+trial+" ["+solutions.size()+"]"+predictedWord+"["+hint+"] ";
            }

            if(trial>6){
                System.out.println(trace);
                System.out.println(trace1);
            }

            solutionNumberOfTrialsToFreq.put(trial, solutionNumberOfTrialsToFreq.getOrDefault(trial,0)+1);
        }
        System.out.println("\ntrial distribution");
        System.out.println(solutionNumberOfTrialsToFreq.toString());

        int solved=0,total=0;
        for(int key : solutionNumberOfTrialsToFreq.keySet()){
            if(key<=6){
                solved+=solutionNumberOfTrialsToFreq.get(key);
            }
            total+=solutionNumberOfTrialsToFreq.get(key);
        }
        System.out.println("Solved="+solved + " | Total="+total+ " | Success rate="+(((double)solved*100)/total));

        System.out.println("\ntrial distribution to achived 2 positional locks:");
        System.out.println(posLocks2ToFreqMap.toString());

        System.out.println("\ntrial distribution to achived 3 positional locks:");
        System.out.println(posLocks3ToFreqMap.toString());

        System.out.println("\ntrial distribution to achived 4 positional locks:");
        System.out.println(posLocks4ToFreqMap.toString());

        /**
         * trial distribution: with global Advisor
         * {1=1, 2=143, 3=1278, 4=2085, 5=1204, 6=538, 7=261, 8=134, 9=67, 10=31, 11=11, 12=4, 13=1, 14=1}
         * trial distribution: solution set specific Advisor
         * {1=1, 2=143, 3=1392, 4=2234, 5=1118, 6=475, 7=216, 8=102, 9=47, 10=22, 11=7, 12=1, 13=1}
         *
         */
    }

    private int numberOfPositionalLocksInHint(String s){
        int n=0;
        for(int i=0; i<s.length(); i++){
            if(s.charAt(i)=='G'){
                ++n;
            }
        }
        return n;
    }
}
