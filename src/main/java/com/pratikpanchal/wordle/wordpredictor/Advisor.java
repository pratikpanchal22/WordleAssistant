package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;
import java.util.*;

public class Advisor {

    private List<String> words;
    private Map<Character, Double> charPercentage;
    private Map<String, Double> wordScoreMap;
    private Map<String, Double> positionalScore = new HashMap<>();
    private Map<Character,Double>[] positionalCharScoreMap;

    public Map<Character, Double>[] getPositionalCharScoreMap() {
        return positionalCharScoreMap;
    }

    public Map<Character, Double> getCharPercentage() {
        return charPercentage;
    }

    public Advisor(List<String> words) {
        this.words = words;
        this.charPercentage = new HashMap<>();

        if(words==null || words.size()==0){
            return;
        }

        int[] frequency = new int[26];
        int totalChars = words.size()*words.get(0).length();
        for(String word : words){
            for(int i=0; i<word.length(); i++){
                frequency[word.charAt(i)-'a']++;
            }
        }

        for(int i=0; i<frequency.length; i++){
            charPercentage.put((char)(i+'a'), (double)(frequency[i]*100)/totalChars);
        }

        this.wordScoreMap = new HashMap<>();
        computeScoreOfWords(words);
        computePositionalScoreOfWords(words);
    }

    public List<String> getWordsWithAtleastKCharacterFrequency(int k){
        List<String> list = new ArrayList<>();
        for(String word : words){
            HashMap<Character, Integer> map = new HashMap<>();
            for(int i=0; i<word.length(); i++){
                map.put(word.charAt(i), map.getOrDefault(word.charAt(i),0)+1);
                if(map.get(word.charAt(i))>=k){
                    list.add(word);
                    break;
                }
            }
        }
        return list;
    }

    public List<Character> getKMostUsedCharacters(int k){

        int[] frequency = new int[26];
        int totalChars = words.size()*words.get(0).length();
        for(String word : words){
            for(int i=0; i<word.length(); i++){
                frequency[word.charAt(i)-'a']++;
            }
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (a,b)-> {
                    return Integer.compare(a[0], b[0]);
                }
        );

//        System.out.println("Letter frequency");
        for(int i=0; i<frequency.length; i++){
            charPercentage.put((char)(i+'a'), (double)(frequency[i]*100)/totalChars);
//            System.out.println((char)(i+'a')+ " f="+frequency[i] + " percentage:"+charPercentage.get((char)(i+'a')));
            pq.add(new int[]{frequency[i], i+'a'});
            if(pq.size()>k){
                pq.remove();
            }
        }

//        System.out.println("\n"+k+" MOST FREQUENT CHARS");
        while(pq.size()>0){
            int[] n = pq.remove();
            System.out.println((char)n[1]+ " frequency="+n[0] + " percentage:"+charPercentage.get((char)(n[1])));
        }

        computeScoreOfWords(words);
        return null;
    }

    public List<WordScoreObject> getAllWordScoreObjects(List<String> words, Optional<Integer> sortConfig){
        List<WordScoreObject> list = new ArrayList<>();
        for(String word : words){
            list.add(new WordScoreObject(word,
                    wordScoreMap.get(word),
                    positionalScore.get(word)));
        }

        switch(sortConfig.orElse(0)){
            case 1:
            case 2:
            case 3:
            case 4:
                sortConfig1ForAttempt1(list);
                break;
            case 5:
                sortConfigForAttempt5(list);
                break;
            case 6:
                sortConfigForAttempt6(list);
                break;
            default:
                sortConfig1ForAttempt1(list);
        }

        //Populate rank
        for(int i=0; i<list.size(); i++){
            list.get(i).setRank(i+1);
        }

        return list;
    }

    private void sortConfigForAttempt6(List<WordScoreObject> list){
        Collections.sort(list, (a,b)->{
            if(a.getPositionalScore()==b.getPositionalScore()){
                if(a.getScore()==b.getScore()) {
                    if(a.getUniqueCharacterCount()==b.getUniqueCharacterCount()){
                        return Integer.compare(b.getRepeatCharacterCount(),a.getRepeatCharacterCount());
                    }
                    return Integer.compare(b.getUniqueCharacterCount(), a.getUniqueCharacterCount());
                }
                return Double.compare(b.getScore(), a.getScore());
            }
            return Double.compare(b.getPositionalScore(), a.getPositionalScore());
        });
    }

    private void sortConfigForAttempt5(List<WordScoreObject> list){
        Collections.sort(list, (a,b)->{
            if(a.getScore()==b.getScore()){
                if(a.getPositionalScore()==b.getPositionalScore()){
                    if(a.getUniqueCharacterCount()==b.getUniqueCharacterCount()){
                        return Integer.compare(b.getRepeatCharacterCount(),a.getRepeatCharacterCount());
                    }
                    return Integer.compare(b.getUniqueCharacterCount(), a.getUniqueCharacterCount());
                }
                return Double.compare(b.getPositionalScore(), a.getPositionalScore());
            }
            return Double.compare(b.getScore(), a.getScore());
        });
    }

    private void sortConfig1ForAttempt1(List<WordScoreObject> list){
        Collections.sort(list, (a,b)->{
            if(a.getUniqueCharacterCount()==b.getUniqueCharacterCount()){
                if(a.getRepeatCharacterCount()==b.getRepeatCharacterCount()){
                    if(a.getPositionalScore()==b.getPositionalScore()){
                        return Double.compare(b.getScore(),a.getScore());
                    }
                    return Double.compare(b.getPositionalScore(), a.getPositionalScore());
                }
                return Integer.compare(b.getRepeatCharacterCount(),a.getRepeatCharacterCount());
            }
            return Integer.compare(b.getUniqueCharacterCount(), a.getUniqueCharacterCount());
        });
    }

    public List<WordScoreObject> getWordScoreObjectsWithoutRepetitiveCharacters1(List<String> words){
        List<WordScoreObject> list = new ArrayList<>();
        for(String word : words) if(!wordHasRepetitiveCharacters(word)){
            list.add(new WordScoreObject(word,
                    wordScoreMap.get(word),
                    positionalScore.get(word)));
        }

        //sort list
        Collections.sort(list, (a,b)-> {
            //if(a.getScore()==b.getScore()){
                return Double.compare(b.getPositionalScore(), a.getPositionalScore());
            //}
            //return (Double.compare(b.getScore(), a.getScore()));
        });

        //Populate rank
        for(int i=0; i<list.size(); i++){
            list.get(i).setRank(i+1);
        }

        return list;
    }

    public String suggestASolution(List<String> sols, Optional<Integer> configId){
        List<WordScoreObject> listOfWordScoreObjs;

//        listOfWordScoreObjs = getWordScoreObjectsWithoutRepetitiveCharacters(sols);
//        if(listOfWordScoreObjs.size()>0){
//            return listOfWordScoreObjs.get(0).getWord();
//        }

        listOfWordScoreObjs = getAllWordScoreObjects(sols, configId);
        if(listOfWordScoreObjs.size()>0){
            return listOfWordScoreObjs.get(0).getWord();
        }

        return null;
    }

    public Double getCharScore(char c){
        return charPercentage.getOrDefault(c,0.0);
    }

    private boolean wordHasRepetitiveCharacters(String word){
        HashSet<Character> set = new HashSet<>();
        for(char c : word.toCharArray()){
            if(set.contains(c)){
                return true;
            }
            set.add(c);
        }
        return false;
    }

    private void computeScoreOfWords(List<String> words){
        for(int i=0; i<words.size(); i++){
            wordScoreMap.put(words.get(i), computeScoreOfWord(words.get(i)));
        }
    }

    /**
     * Compute the positionalScore of the give words
     * positionalScore of a word is the sum of all the positionalScores of it's characters
     * positionalScore of the character at the given position is defined as:
     *               freq / number of total words
     * where freq = number of times the character appears at that position
     * @param words
     */
    private void computePositionalScoreOfWords(List<String> words){
        /**
         * row represents the position/index
         * column represents the character
         */
        int[][] positionalCharFrequencies = new int[5][26];

        for(String word : words){
            for(int i=0; i<word.length(); i++){
                positionalCharFrequencies[i][word.charAt(i)-'a']++;
            }
        }
        positionalCharScoreMap = new HashMap[5];
        for(int i=0; i<positionalCharScoreMap.length; i++){
            positionalCharScoreMap[i] = new HashMap<Character,Double>();
        }

        for(int r=0; r<5; r++){
            for(int c=0; c<26; c++){
                positionalCharScoreMap[r].put((char)(c+'a'),
                        ((double)positionalCharFrequencies[r][c]/(double)words.size()));
            }
        }

        for(String word : words){
            positionalScore.put(word, computePositionalScoreOfWord(word, positionalCharScoreMap));
        }
    }

    private double computePositionalScoreOfWord(String word, Map<Character, Double>[] positionalCharScoreMap) {
        double score = 0.0;
        for(int i=0; i<word.length(); i++){
            score+=positionalCharScoreMap[i].get(word.charAt(i));
        }
        return score;
    }

    private double computeScoreOfWord(String word){
        double score=0;
        for(int i=0; i<word.length(); i++){
            score+=charPercentage.get(word.charAt(i));
        }
        return score;
    }
}
