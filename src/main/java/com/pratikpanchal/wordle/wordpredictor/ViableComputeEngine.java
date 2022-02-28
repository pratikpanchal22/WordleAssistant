package com.pratikpanchal.wordle.wordpredictor;

import java.util.*;

public class ViableComputeEngine {

    /**
     * priorityCharsScoreMap is the map of all the characters (to their score). Some or all of
     * these characters (keys) needs to be used to find strings from the given 'wordSet'.
     * These words are known as viable strings.
     *
     * wordSet is the set of all words from which the viable strings are chosen.
     *
     * viableStringObjectsList is the list of ViableStringObject (vso) (see below) that is sorted as follows:
     *
     * (1) In the DECREASING order of vso.upcc
     * (2) if, vso.upcc is same for multiple objects,
     *     in the DECREASING order of vso.rpcc
     * (3) if, vso.rpcc is same for multiple objects,
     *     in the INCREASING order of vso.npcc
     * (4) if, vso.npcc is same for multiple objects,
     *     in the DECREASING order of vso.pcs
     *
     * vso.pcs should be same for different permutations of the same string
     * (unless, the priority characters have been assigned the same score - the check of
     * which is outside the scope of this class)
     * i.e. if vso.pcs is same for multiple strings, it implies that they all are permutations of
     * same set of priority characters and hence their order does not matter for the use-case
     * intention of this compute module.
     */

    final private Map<Character, Double> charPercentage;
    final private Set<String> wordSet;
    final private List<ViableStringObject> viableStringObjectsList;
    final private Set<Character> priorityCharsSet;

    public List<ViableStringObject> getViableStringObjectsList() {
        return viableStringObjectsList;
    }

    public class ViableStringObject {

        /**
         *
         * For each word, these are the following 4 values that are computed:
         *
         * (1) upcc: Unique Priority Character Count
         * upcc is the count of all unique priority characters present in the string
         * example: for the given set of priority characters: a,b,c,d,e
         *          for 'abcde', upcc=5
         *          for 'aabbc', upcc=3
         *          for 'fghij', upcc=0
         *
         * (2) rpcc: Repeat Priority Character Count
         * rpcc is the count of all priority characters that appear more than once
         * i.e. has frequency>1
         * example: for the given set of priority characters: a,b,c,d,e
         *          for 'abcde', rpcc=0
         *          for 'aabcd', rpcc=1
         *          for 'aabbc', rpcc=2
         *          for 'aaabb', rpcc=2
         *          for 'aaaff', rpcc=1
         *          for 'aabff', rpcc=1
         *          for 'abfff', rpcc=0
         *
         * (3) npcc: Non-Priority Character Count
         * npcc is the count of all non-priority characters (including repeats)
         * present in the string
         * example: for the given set of priority characters: a,b,c,d,e
         *          for 'abcde', npcc=0
         *          for 'abcdf', npcc=1
         *          for 'abcff', npcc=2
         *          for 'abffg', npcc=3
         *
         * (4) pccs: Priority Character Score
         * pccs is the score computed based on the presence of priority characters.
         * pccs stands for Priority Character Combined Score.
         * The algorithm to compute pcs is as follows:
         * pcs of a string is defined as sum of score of each priority character present in the string
         * The score of non-priority character is 0
         * example: for the given priority character -> score mapping: {a=10.43, b=12.22, c=10, d=5, e=20}
         *          pcs for string 'afghi' is 10.43
         *          pcs for string 'addfg' is 20.43
         *          pcs for string 'vwxyz' is 00.00
         *
         */
        private String word;
        private int upcc;
        private int rpcc;
        private int npcc;
        private double pccs;

        public String getWord() {
            return word;
        }

        public int getUpcc() {
            return upcc;
        }

        public int getRpcc() {
            return rpcc;
        }

        public int getNpcc() {
            return npcc;
        }

        public double getPccs() {
            return pccs;
        }

        public ViableStringObject(String word, int upcc, int rpcc, int npcc, double pccs) {
            this.word = word;
            this.upcc = upcc;
            this.rpcc = rpcc;
            this.npcc = npcc;
            this.pccs = pccs;
        }

        @Override
        public String toString() {
            return "ViableStringObject{" +
                    "word='" + word + '\'' +
                    ", upcc=" + upcc +
                    ", rpcc=" + rpcc +
                    ", npcc=" + npcc +
                    ", pccs=" + pccs +
                    '}'+'\n';
        }
    }

    public ViableComputeEngine(Map<Character, Double> charPercentage,
                               List<Character> priorityCharsList,
                               List<String> wordList){
        this.charPercentage = charPercentage;
        this.priorityCharsSet = new HashSet<Character>(priorityCharsList);
        this.wordSet = new HashSet<String>(wordList);
        viableStringObjectsList = new ArrayList<ViableStringObject>();

        compute();
    }

    private void compute(){
        for(String word : wordSet){
            int nonPriorityCharCount=0;
            int repeatPriorityCharCount=0;
            double priorityCharCombinedScore=0;

            //map: character -> frequency
            Map<Character,Integer> pCharMap = new HashMap<>();

            for(int i=0; i<word.length(); i++){
                char c = word.charAt(i);
                if(priorityCharsSet.contains(c)){
                    pCharMap.put(c, pCharMap.getOrDefault(c,0)+1);
                }
                else {
                    nonPriorityCharCount++;
                }
            }

            //compute: rpcc (repeat priority char count)
            //compute: pccs (priority char combined score)
            for(Character key : pCharMap.keySet()){
                if(pCharMap.get(key)>1){
                    ++repeatPriorityCharCount;
                }
                priorityCharCombinedScore+=charPercentage.get(key);
            }

//            for(int i=0; i<word.length(); i++){
//                //we can compute solely based on priority char/positions
//                if(false){
//                    priorityCharCombinedScore+=priorityCharsSet.contains(word.charAt(i))?
//                            charPercentage.get(word.charAt(i)):0;
//                }
//                //or we can compute based on all char/positions
//                else {
//                    priorityCharCombinedScore+= charPercentage.get(word.charAt(i));
//                }
//            }

            if(pCharMap.size()>1){
                viableStringObjectsList.add(
                        new ViableStringObject(
                                word,
                                pCharMap.size(),
                                repeatPriorityCharCount,
                                nonPriorityCharCount,
                                priorityCharCombinedScore)
                );
            }
        }

        //sort the computedList
        Collections.sort(viableStringObjectsList, (n1, n2)->{
            if(n1.pccs==n2.pccs){
                if(n1.upcc==n2.upcc){
                    if(n1.rpcc==n2.rpcc){
                        return Integer.compare(n1.npcc, n2.npcc);
                    }
                    return Integer.compare(n2.rpcc, n1.rpcc);
                }
                return Integer.compare(n2.upcc, n1.upcc);
            }
            return Double.compare(n2.pccs,n1.pccs);
        });
    }
}
