package com.pratikpanchal.wordle.wordpredictor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ComputationalEngine {
    public List<String> compute(TrieNode root, ComputationalInputs computationalInputs){

        //validations
        if(root==null ||
                computationalInputs.getGlobalExclusions()==null ||
                computationalInputs.getPositionalExclusions()==null ||
                computationalInputs.getPositionalLocks()==null){
            throw new IllegalArgumentException("Input is invalid");
        }

        //Prepare for recursion
        List<String> list = new ArrayList<>();
        char[] candidate = new char[5];
        Queue<TrieNode> q = new LinkedList<>();
        q.add(root);

        computeRecursively(q, list, candidate, 0, computationalInputs);
        return list;
    }

    private void computeRecursively(Queue<TrieNode> q, List<String> list, char[] candidate,
                                    int idx, ComputationalInputs ci){
        //base case
        if(idx==5){

//            System.out.println("mandatory inclusions:"+ci.getMandatoryInclusions().toString());

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

            //check if candidate complies with maxCharacterCount
            for(char key : ci.getMaxCharacterCount().keySet()){
                if(candidateLetterCount[(int)(key-'a')]>ci.getMaxCharacterCount().get(key)){
//                    System.out.println("Rejecting due to over abundance of character: "+key+". Allowed:"+
//                            ci.getMaxCharacterCount().get(key) + " Actual:"+candidateLetterCount[key-'a']
//                            +" :"+String.valueOf(candidate));
                    return;
                }
            }

            //check if candidate complies with mandatoryInclusions
            for(char c : ci.getMandatoryInclusions()){
                if(candidateLetterCount[c-'a']==0){
//                    System.out.println("Rejecting due to missing mandatory inclusion: '"+c+"'. "+String.valueOf(candidate));
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
            TrieNode n = q.remove();

            /**
             * At each idx,
             * (1) we first check the positionalLocks array
             * If there is a character at the desired idx, we go down that particular
             * node in the com.pratikpanchal.wordle.wordpredictor.Trie
             * (2) Otherwise, we have to consider every node that
             *     (a) belongs to inclusions, and
             *     (b) does not belong to positionalExclusions
             */
            if(ci.getPositionalLocks()[idx]!='\0'){
                //there can be only 1 letter here which is positionalLocks[idx]
                candidate[idx]=ci.getPositionalLocks()[idx];
                if(n.next[candidate[idx]-'a']!=null){
                    q.add(n.next[candidate[idx]-'a']);
                    computeRecursively(q, list, candidate, idx+1, ci);
                }
            }
            else {
                //check inclusions & positionalExclusions
                for(int i=0; i<26; i++) if(!ci.getGlobalExclusions().contains((char)(i+'a')) && !ci.getPositionalExclusions()[idx].contains((char)(i+'a'))) {
                    if(n.next[i]!=null){
                        candidate[idx]=n.next[i].c;
                        q.add(n.next[i]);
                        computeRecursively(q, list, candidate, idx+1, ci);
                    }
                }
            }
        }
    }
}
