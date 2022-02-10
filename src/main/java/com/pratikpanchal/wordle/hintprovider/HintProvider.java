package com.pratikpanchal.wordle.hintprovider;

import java.util.Arrays;

public class HintProvider {
    private String secretWord;

    public HintProvider(String secretWord) {
        this.secretWord = secretWord;
    }

    public String provideHint(String guessedWord){

        //validations
        if(guessedWord==null || guessedWord.length()!=secretWord.length()){
            throw new IllegalArgumentException("Length of guessed word (" + guessedWord +
                    ") does NOT MATCH the length of secret word ("+secretWord.length()+")");
        }

        char[] hint = new char[secretWord.length()];
        Arrays.fill(hint, 'B');

        //character count array
        int[] charCount = new int[26];

        //first fill all matches & count the unmatched characters
        for(int i=0; i<secretWord.length(); i++){
            if(secretWord.charAt(i)==guessedWord.charAt(i)){
                hint[i]='G';
            }
            else {
                charCount[secretWord.charAt(i)-'a']++;
            }
        }

        //second, inspect all the unmatched positions of guessedWord and check if
        // it exists anywhere in the secret word
        for(int i=0; i<hint.length; i++) if(hint[i]=='B'){
            int idx=guessedWord.charAt(i)-'a';
            if(charCount[idx]>0){
                --charCount[idx];
                hint[i]='Y';
            }
        }

        return String.valueOf(hint);
    }
}
