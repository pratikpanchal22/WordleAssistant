//package com.example.idea;
import java.util.*;

public class Advisor {

    private List<String> words;
    private HashMap<Character, Double> charPercentage;
    private HashMap<String, Double> wordScoreMap;

    public Advisor(List<String> words) {
        this.words = words;
        this.charPercentage = new HashMap<>();
        this.wordScoreMap = new HashMap<>();
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

        System.out.println("Letter frequency");
        for(int i=0; i<frequency.length; i++){
            charPercentage.put((char)(i+'a'), (double)(frequency[i]*100)/totalChars);
            System.out.println((char)(i+'a')+ " f="+frequency[i] + " percentage:"+charPercentage.get((char)(i+'a')));
            pq.add(new int[]{frequency[i], i+'a'});
            if(pq.size()>k){
                pq.remove();
            }
        }

        System.out.println("\n"+k+" MOST FREQUENT CHARS");
        while(pq.size()>0){
            int[] n = pq.remove();
            System.out.println((char)n[1]+ " frequency="+n[0] + " percentage:"+charPercentage.get((char)(n[1])));
        }

        computeScoreOfWords(words);
        return null;
    }

    public List<WordScoreObject> getScoresOfWords(List<String> words){
        List<WordScoreObject> list = new ArrayList<>();
        for(String word : words){
            list.add(new WordScoreObject(word, wordScoreMap.get(word)));
        }

        //sort list
        Collections.sort(list, (a,b)-> {
            return (Double.compare(b.score, a.score));
        });

        return list;
    }

    private void computeScoreOfWords(List<String> words){
        for(int i=0; i<words.size(); i++){
            wordScoreMap.put(words.get(i), computeScoreOfWord(words.get(i)));
        }
    }

    private double computeScoreOfWord(String word){
        double score=0;
        for(int i=0; i<word.length(); i++){
            score+=charPercentage.get(word.charAt(i));
        }
        return score;
    }
}
