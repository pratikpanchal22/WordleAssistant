package com.pratikpanchal.wordle.wordpredictor;

import java.util.*;

public class ComputationalInputs {

    static final char COLOR_BLACK = 'B';
    static final char COLOR_YELLOW = 'Y';
    static final char COLOR_GREEN = 'G';

    //
    /**
     * globalExclusions:::List<Character>
     * These characters are confirmed to be NOT PRESENT in the final
     * word
     * In Wordle, these letters appear in BLACK
     */
    private Set<Character> globalExclusions;

    /**
     * positionalExclusions
     * These letters DO EXIST in the word, but are confirmed to NOT
     * EXIST at the given positions (list of indices)
     * In Wordle, these letters appear in ORANGE
     */
    private Set<Character>[] positionalExclusions;
    //character->list of indices where it is not supposed to be
    private HashMap<Character, Set<Integer>> positionalExclusionMap;

    /**
     * positionalLocks:::char[5]
     * These are the positions in which a letter is confirmed to be
     * In Wordle, these letters appear in GREEN
     */
    private char[] positionalLocks;
    private boolean[] positionalLockAlreadyCountedForMandatoryInclusions;

    /**
     * mandatoryInclusions:::List<Character>
     * These are the characters that are confirmed to be in the final words
     * These inclused the characters from positionalLocks, and
     * positionalExclusions with correct frequency
     */
    private List<Character> mandatoryInclusions;

    /**
     * maxCharacterCount:::HashMap<Character,Integer>
     * These are maximum number of characters that could be in the final word
     */
    private HashMap<Character,Integer> maxCharacterCount;

    private InputGrid inputGrid;

    public ComputationalInputs(InputGrid inputGrid) {
        this.inputGrid = inputGrid;
        this.globalExclusions = new HashSet<>();
        this.maxCharacterCount = new HashMap<>();
        this.mandatoryInclusions = new ArrayList<>();

        //TODO: Get rid of magic number below
        this.positionalLocks = new char[5];
        positionalLockAlreadyCountedForMandatoryInclusions = new boolean[5];
        //TODO: Get rid of magic char below
        Arrays.fill(positionalLocks, '\0');

        //TODO: Get rid of the magic number below;
        positionalExclusions = new HashSet[5];
        for(int i=0; i<positionalExclusions.length; i++){
            positionalExclusions[i]=new HashSet<>();
        }
        positionalExclusionMap = new HashMap<>();

        this.generatePositionalExclusionListAndPositionalLocksAndGlobalExclusionList();
    }

    private void generatePositionalExclusionListAndPositionalLocksAndGlobalExclusionList(){

        HashMap<Character, Integer> charMandatoryInclusionCountMap = new HashMap<>();
        for(int i=0; i<this.inputGrid.grid.size(); i++){
            char[] charRow = this.inputGrid.grid.get(i)[InputGrid.CHAR_ARRAY_IDX];
            char[] colorRow = this.inputGrid.grid.get(i)[InputGrid.COLOR_ARRAY_IDX];

            /**
             * Start with evaluating the yellow characters (i.e. the ones that are positionally incorrect)
             */
            HashSet<Character> charsThatAreYellowInThisRow = new HashSet<>();
            HashMap<Character,Integer> charsThatAreYellowInThisRowMap = new HashMap<>();
            for(int j=0; j<charRow.length; j++) if(colorRow[j]==COLOR_YELLOW){
                positionalExclusions[j].add(charRow[j]);
                if(!positionalExclusionMap.containsKey(charRow[j])){
                    positionalExclusionMap.put(charRow[j], new HashSet<>());
                    mandatoryInclusions.add(charRow[j]);
                }
                positionalExclusionMap.get(charRow[j]).add(j);

                charsThatAreYellowInThisRow.add(charRow[j]);
                charsThatAreYellowInThisRowMap.put(charRow[j], charsThatAreYellowInThisRowMap.getOrDefault(charRow[j],0)+1);
            }

            /**
             * Then, evaluate the green characters (i.e. the ones that are positionally correct)
             */
            HashSet<Character> charsThatAreGreenInThisRow = new HashSet<>();
            HashMap<Character,Integer> charsThatAreGreenInThisRowMap = new HashMap<>();
            for(int j=0; j<charRow.length; j++) if(colorRow[j]==COLOR_GREEN){
                //TODO: Get rid of magic char below
                if(positionalLocks[j]!='\0' && positionalLocks[j]!=charRow[j]){
                    throw new InputMismatchException("Positional Lock is already occupied with character:"+positionalLocks[j] + " and an attempt was made to overwrite it with a new character: "+charRow[j] + " from input row:"+i+"\nOffending Input row: charRow"+Arrays.toString(charRow)+" colorRow:"+Arrays.toString(colorRow));
                }
                positionalLocks[j] = charRow[j];
                charsThatAreGreenInThisRow.add(charRow[j]);
                charsThatAreGreenInThisRowMap.put(charRow[j], charsThatAreGreenInThisRowMap.getOrDefault(charRow[j],0)+1);

                /**
                 * add this to mandatoryInclusions if
                 * (1) it's not already counted, or,
                 * (2) it's not already in mandatoryInclusions
                 * (3) it's in mandatory inclusions and also in charsThatAreYellowInThisRow
                 */
                if(positionalLockAlreadyCountedForMandatoryInclusions[j]){
                    continue;
                }

                if(!mandatoryInclusions.contains(charRow[j])){
                    mandatoryInclusions.add(charRow[j]);
                }
                else if(charsThatAreYellowInThisRow.contains(charRow[j])){
                    mandatoryInclusions.add(charRow[j]);
                }
                positionalLockAlreadyCountedForMandatoryInclusions[j]=true;
            }

            /**
             * Experimental mandatoryInclusionsList
             */
            for(int j=0; j<charRow.length; j++){
                charMandatoryInclusionCountMap.put(charRow[j],
                        Math.max(
                                charMandatoryInclusionCountMap.getOrDefault(charRow[j],0),
                                    (charsThatAreYellowInThisRowMap.getOrDefault(charRow[j],0)+
                                    charsThatAreGreenInThisRowMap.getOrDefault(charRow[j],0))
                        )
                );
            }
            List<Character> localMandatoryInclusions = new ArrayList<>();
            for(char key : charMandatoryInclusionCountMap.keySet()){
                int freq = charMandatoryInclusionCountMap.get(key);
                while(freq-- > 0){
                    localMandatoryInclusions.add(key);
                }
            }
            mandatoryInclusions=localMandatoryInclusions;

            /**
             * And finally, evaluate the black characters (i.e. the ones that are not present in the final word)
             */
            for(int j=0; j<charRow.length; j++) if(colorRow[j]==COLOR_BLACK){
                if(!charsThatAreGreenInThisRow.contains(charRow[j]) && !charsThatAreYellowInThisRow.contains(charRow[j])){
                    this.globalExclusions.add(charRow[j]);
                }else {
                    /**
                     * if here: This character is marked as not present (black) in the final word even though
                     * there are other instances of it that are either YELLOW or GREEN in this same row.
                     * That means, we have found the maximum frequency of this character that is allowed in the final
                     * word.
                     * The maximum frequency is the frequency of this character in mandatoryInclusions list
                     */
                    int freq=0;
                    for(char c : mandatoryInclusions){
                        if(c==charRow[j]){
                            freq++;
                        }
                    }
                    maxCharacterCount.put(charRow[j], freq);
                }
            }
        }
    }

    private void addToMandatoryInclusions(char c){
        if(this.mandatoryInclusions.size()==5){
            throw new RuntimeException("mandatoryInclusions size is already 5. Trying to add "+c);
        }
        this.mandatoryInclusions.add(c);
    }

    //////////////////////////////////////////
    //Getters
    //////////////////////////////////////////
    public Set<Character> getGlobalExclusions() {
        return globalExclusions;
    }

    public Set<Character>[] getPositionalExclusions() {
//        int pos=0;
//        for(Set<Character> s : positionalExclusions){
//            System.out.println("Pos:"+pos+"..."+s.toString());
//            pos++;
//        }
        return positionalExclusions;
    }

    public char[] getPositionalLocks() {
//        System.out.println("positionalLocks:"+String.valueOf(positionalLocks));
        return positionalLocks;
    }

    public List<Character> getMandatoryInclusions() {
//        System.out.println("mandatoryInclusions:"+mandatoryInclusions.toString());
        return mandatoryInclusions;
    }

    public HashMap<Character, Integer> getMaxCharacterCount() {
//        System.out.println("maxCharacterCount: "+maxCharacterCount.toString());
        return maxCharacterCount;
    }
}
