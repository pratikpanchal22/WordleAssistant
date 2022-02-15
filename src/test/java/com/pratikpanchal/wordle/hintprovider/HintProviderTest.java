package com.pratikpanchal.wordle.hintprovider;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test: HintProvider behavior")
public class HintProviderTest {

    @Test
    @DisplayName("Test: Secret word with no repeating characters")
    public void uniqueCharsTest(){
        HintProvider hintProvider = new HintProvider(HintProvider.WORD_WATER);

        assertEquals(HintProvider.HINT_BGBYB, hintProvider.provideHint(HintProvider.WORD_SALTY));
        assertEquals(HintProvider.HINT_YGBBB, hintProvider.provideHint(HintProvider.WORD_TASTY));
        //TODO: Add more tests uniqueCharsTest
    }

    @Test
    @DisplayName("Test: Secret word with 1 repeating character of frequency 2")
    public void repeatingChars1Test(){
        HintProvider hintProvider = new HintProvider(HintProvider.WORD_TASTY);

        assertEquals(HintProvider.HINT_BGYBB, hintProvider.provideHint(HintProvider.WORD_WATER));
        assertEquals(HintProvider.HINT_GBBGY, hintProvider.provideHint(HintProvider.WORD_TTXTS));
        //TODO: Add more tests to repeatingChars1Test
    }

    @Test
    @DisplayName("Test: Secret word with 2 repeating characters of frequency 2")
    public void repeatingChars2Test(){
        HintProvider hintProvider = new HintProvider("ababc");

        assertEquals(HintProvider.HINT_GYYGG, hintProvider.provideHint(HintProvider.WORD_AABBC));
        assertEquals(HintProvider.HINT_GYGGY, hintProvider.provideHint(HintProvider.WORD_ACABB));
        assertEquals(HintProvider.HINT_YYGGY, hintProvider.provideHint(HintProvider.WORD_CAABB));
        assertEquals(HintProvider.HINT_GBGBB, hintProvider.provideHint(HintProvider.WORD_AAAAA));
        assertEquals(HintProvider.HINT_BGBGB, hintProvider.provideHint(HintProvider.WORD_BBBBB));
        assertEquals(HintProvider.HINT_BBBBG, hintProvider.provideHint(HintProvider.WORD_CCCCC));
        assertEquals(HintProvider.HINT_BBBBB, hintProvider.provideHint(HintProvider.WORD_XXXXX));
        assertEquals(HintProvider.HINT_GGGGG, hintProvider.provideHint(HintProvider.WORD_ABABC));
        //TODO: Add more tests to repeatingChars1Test
    }

    @Test
    @DisplayName("Test: Hint provider initialized with secret word in all UPPER CASE")
    public void secretWordCaseMisMatchTest(){
        HintProvider hintProvider = new HintProvider(HintProvider.WORD_TASTY.toUpperCase());

        assertEquals(HintProvider.HINT_BGYBB, hintProvider.provideHint(HintProvider.WORD_WATER));
        assertEquals(HintProvider.HINT_GBBGY, hintProvider.provideHint(HintProvider.WORD_TTXTS));
        //TODO: Add more tests to secretWordCaseMisMatchTest
    }

    @Test
    @DisplayName("Test: Hint provider provideHint method invoked with words in all UPPER CASE")
    public void guessedWordCaseMisMatchTest(){
        HintProvider hintProvider = new HintProvider(HintProvider.WORD_TASTY);

        assertEquals(HintProvider.HINT_BGYBB, hintProvider.provideHint(HintProvider.WORD_WATER.toUpperCase()));
        assertEquals(HintProvider.HINT_GBBGY, hintProvider.provideHint(HintProvider.WORD_TTXTS.toUpperCase()));
        //TODO: Add more tests to guessedWordCaseMisMatchTest

    }

    //TODO: Add more tests for HintProviderTest
}
