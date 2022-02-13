package com.pratikpanchal.wordle.hintprovider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
//import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test: HintProvider behavior")
public class HintProviderTest {

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

    @Test
    @DisplayName("Test: Secret word with no repeating characters")
    public void uniqueCharsTest(){
        HintProvider hintProvider = new HintProvider(WORD_WATER);

        assertEquals(HINT_BGBYB, hintProvider.provideHint(WORD_SALTY));
        assertEquals(HINT_YGBBB, hintProvider.provideHint(WORD_TASTY));
        //TODO: Add more tests uniqueCharsTest
    }

    @Test
    @DisplayName("Test: Secret word with 1 repeating character of frequency 2")
    public void repeatingChars1Test(){
        HintProvider hintProvider = new HintProvider(WORD_TASTY);

        assertEquals(HINT_BGYBB, hintProvider.provideHint(WORD_WATER));
        assertEquals(HINT_GBBGY, hintProvider.provideHint(WORD_TTXTS));
        //TODO: Add more tests to repeatingChars1Test
    }

    @Test
    @DisplayName("Test: Secret word with 2 repeating characters of frequency 2")
    public void repeatingChars2Test(){
        HintProvider hintProvider = new HintProvider("ababc");

        assertEquals(HINT_GYYGG, hintProvider.provideHint(WORD_AABBC));
        assertEquals(HINT_GYGGY, hintProvider.provideHint(WORD_ACABB));
        assertEquals(HINT_YYGGY, hintProvider.provideHint(WORD_CAABB));
        assertEquals(HINT_GBGBB, hintProvider.provideHint(WORD_AAAAA));
        assertEquals(HINT_BGBGB, hintProvider.provideHint(WORD_BBBBB));
        assertEquals(HINT_BBBBG, hintProvider.provideHint(WORD_CCCCC));
        assertEquals(HINT_BBBBB, hintProvider.provideHint(WORD_XXXXX));
        assertEquals(HINT_GGGGG, hintProvider.provideHint(WORD_ABABC));
        //TODO: Add more tests to repeatingChars1Test
    }

    @Test
    @DisplayName("Test: Hint provider initialized with secret word in all UPPER CASE")
    public void secretWordCaseMisMatchTest(){
        HintProvider hintProvider = new HintProvider(WORD_TASTY.toUpperCase());

        assertEquals(HINT_BGYBB, hintProvider.provideHint(WORD_WATER));
        assertEquals(HINT_GBBGY, hintProvider.provideHint(WORD_TTXTS));
        //TODO: Add more tests to secretWordCaseMisMatchTest
    }

    @Test
    @DisplayName("Test: Hint provider provideHint method invoked with words in all UPPER CASE")
    public void guessedWordCaseMisMatchTest(){
        HintProvider hintProvider = new HintProvider(WORD_TASTY);

        assertEquals(HINT_BGYBB, hintProvider.provideHint(WORD_WATER.toUpperCase()));
        assertEquals(HINT_GBBGY, hintProvider.provideHint(WORD_TTXTS.toUpperCase()));
        //TODO: Add more tests to guessedWordCaseMisMatchTest
    }

    //TODO: Add more tests for HintProviderTest
}
