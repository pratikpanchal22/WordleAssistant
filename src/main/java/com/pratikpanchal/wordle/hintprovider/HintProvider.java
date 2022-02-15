package com.pratikpanchal.wordle.hintprovider;

import java.util.Arrays;

public class HintProvider {

    public static final String HINT_GGGGG_ALL_MATCH = "GGGGG";
    public static final String HINT_BBBBB_NO_MATCH = "BBBBB";
    public static final String HINT_BGBYB = "BGBYB";
    public static final String HINT_YGBBB = "YGBBB";
    public static final String WORD_WATER = "water";
    public static final String WORD_SALTY = "salty";
    public static final String WORD_TASTY = "tasty";
    public static final String HINT_BGYBB = "BGYBB";
    public static final String HINT_GBBGY = "GBBGY";
    public static final String WORD_TTXTS = "ttxts";
    public static final String HINT_GYYGG = "GYYGG";
    public static final String HINT_GYGGY = "GYGGY";
    public static final String HINT_YYGGY = "YYGGY";
    public static final String HINT_GBGBB = "GBGBB";
    public static final String HINT_BGBGB = "BGBGB";
    public static final String HINT_BBBBG = "BBBBG";
    public static final String HINT_BBBBB = "BBBBB";
    public static final String HINT_GGGGG = "GGGGG";
    public static final String WORD_AABBC = "aabbc";
    public static final String WORD_ACABB = "acabb";
    public static final String WORD_CAABB = "caabb";
    public static final String WORD_AAAAA = "aaaaa";
    public static final String WORD_BBBBB = "bbbbb";
    public static final String WORD_CCCCC = "ccccc";
    public static final String WORD_XXXXX = "xxxxx";
    public static final String WORD_ABABC = "ababc";

    private String secretWord;

    public HintProvider(String secretWord) {
        this.secretWord = secretWord.toLowerCase();
    }

    public String provideHint(String guessedWord){

        //validations
        if(guessedWord==null || guessedWord.length()!=secretWord.length()){
            throw new IllegalArgumentException("Length of guessed word (" + guessedWord +
                    ") does NOT MATCH the length of secret word ("+secretWord.length()+")");
        }

        //Guessed words are always in lowercase
        guessedWord = guessedWord.toLowerCase();

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
