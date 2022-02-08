//package com.example.idea;

import tools.WordImporter;

import java.util.*;

public class Main {

    public static void main(String[] args) {

        System.out.println("user directory: " + System.getProperty("user.dir"));

        String wordFile = "src/main/resources/wordList_5Letter.txt";
        WordImporter wi = new WordImporter();

        List<String> words = wi.importWords(wordFile);

        //Advisor
        Advisor advisor = new Advisor(words);
        List<Character> l = advisor.getKMostUsedCharacters(26);

        //Create Trie
        Node root = Trie.addWordsToTrie(words);

        //Create new InputGrid
        InputGrid inputGrid = new InputGrid();
        inputGrid.addRow("arose","BBBBG");
        inputGrid.addRow("tilde","BBGYG");
//        inputGrid.addRow("laird","YBGGB");
//        inputGrid.addRow("gnaws","BBGBG");


        ComputationalInputs computationalInputs = new ComputationalInputs(inputGrid);

        List<String> solution = compute(root,
                computationalInputs.getGlobalExclusions(),
                computationalInputs.getPositionalExclusions(),
                computationalInputs.getPositionalLocks(),
                computationalInputs.getMandatoryInclusions());

        System.out.println("\nRanked solution with scores: Total size:"+solution.size());
        System.out.println(advisor.getAllWordScoreObjects(solution).toString());

        List<WordScoreObject> wordScoreObjects = advisor.getWordScoreObjectsWithoutRepetitiveCharacters(solution);
        System.out.println("\nRanked solution with scores (unique characters only): Size:"+ wordScoreObjects.size());
        System.out.println(wordScoreObjects.toString());
    }

    private static List<String> compute(Node root, List<Character> exclusions,
                                        Set<Character>[] positionalExclusions,
                                        char[] positionalLocks,
                                        List<Character> mandatoryInclusions){

        //validations
        if(root==null || exclusions==null || positionalExclusions==null || positionalLocks==null){
            throw new IllegalArgumentException("Input is invalid");
        }

        //compute inclusions from exclusions
        boolean[] inclusions = new boolean[26];
        Set<Character> excludedChars = new HashSet<>(exclusions);
        for(char c='a'; c<='z'; c++){
            if(!excludedChars.contains(c)){
                inclusions[c-'a']=true;
            }
        }

        //Prepare for recursion
        List<String> list = new ArrayList<>();
        char[] candidate = new char[5];
        Queue<Node> q = new LinkedList<>();
        q.add(root);

        computeRecursively(positionalLocks, inclusions, q, positionalExclusions, list, mandatoryInclusions, candidate, 0);
        return list;
    }

    private static void computeRecursively(char[] positionalLocks,
                                    boolean[] inclusions,
                                    Queue<Node> q,
                                    Set<Character>[] positionalExclusions,
                                    List<String> list,
                                    List<Character> mandatoryInclusions,
                                    char[] candidate,
                                    int idx ){
        //base case
        if(idx==5){

            /**
             * We have constructed a 5 character word (candidate) at this point
             * If this candidate has all the characters that are listed in
             * mandatoryInclusions list, add the candidate to our final list
             *
             * construct an array of candidateLetterCount that keeps a frequency
             * of all characters in candidate.
             * Then, for each character in mandatoryInclusions, deduct the frequency
             * count from the candidateLetterCount.
             * If at any point, the frequency goes below 0, we don't have the
             * mandatory characters in our candidate => reject the candidate
             */
            int[] candidateLetterCount = new int[26];
            for(char c : candidate){
                candidateLetterCount[c-'a']++;
            }

            for(char c : mandatoryInclusions){
                if(candidateLetterCount[c-'a']==0){
                    System.out.println("Rejecting: "+String.valueOf(candidate));
                    return;
                }
                --candidateLetterCount[c-'a'];
            }

            list.add(String.valueOf(candidate));
            return;
        }

        //core
        int size = q.size();
        while(size-- > 0){
            Node n = q.remove();

            /**
             * At each idx,
             * (1) we first check the positionalLocks array
             * If there is a character at the desired idx, we go down that particular
             * node in the Trie
             * (2) Otherwise, we have to consider every node that
             *     (a) belongs to inclusions, and
             *     (b) does not belong to positionalExclusions
             */
            if(positionalLocks[idx]!='\0'){
                //there can be only 1 letter here which is positionalLocks[idx]
                candidate[idx]=positionalLocks[idx];
                if(n.next[candidate[idx]-'a']!=null){
                    q.add(n.next[candidate[idx]-'a']);
                    computeRecursively(positionalLocks, inclusions, q,
                            positionalExclusions, list,
                            mandatoryInclusions, candidate, idx+1);
                }
            }
            else {
                //check inclusions & positionalExclusions
                for(int i=0; i<inclusions.length; i++) if(inclusions[i] && !positionalExclusions[idx].contains((char)(i+'a'))) {
                    if(n.next[i]!=null){
                        candidate[idx]=n.next[i].c;
                        q.add(n.next[i]);
                        computeRecursively(positionalLocks, inclusions, q,
                                positionalExclusions, list,
                                mandatoryInclusions, candidate, idx+1);
                    }
                }
            }
        }
    }
}
