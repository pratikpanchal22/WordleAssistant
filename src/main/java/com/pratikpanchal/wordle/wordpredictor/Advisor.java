package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;
import java.util.*;

public class Advisor {

    private List<String> words;
    private HashMap<Character, Double> charPercentage;
    private HashMap<String, Double> wordScoreMap;

    public Advisor(List<String> words) {
        this.words = words;
        this.charPercentage = new HashMap<>();

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

    public List<WordScoreObject> getAllWordScoreObjects(List<String> words){
        List<WordScoreObject> list = new ArrayList<>();
        for(String word : words){
            list.add(new WordScoreObject(word, wordScoreMap.get(word)));
        }

        //sort list
        Collections.sort(list, (a,b)-> {
            return (Double.compare(b.getScore(), a.getScore()));
        });

        //Populate rank
        for(int i=0; i<list.size(); i++){
            list.get(i).setRank(i+1);
        }

        return list;
    }

    public List<WordScoreObject> getWordScoreObjectsWithoutRepetitiveCharacters(List<String> words){
        List<WordScoreObject> list = new ArrayList<>();
        for(String word : words) if(!wordHasRepetitiveCharacters(word)){
            list.add(new WordScoreObject(word, wordScoreMap.get(word)));
        }

        //sort list
        Collections.sort(list, (a,b)-> {
            return (Double.compare(b.getScore(), a.getScore()));
        });

        //Populate rank
        for(int i=0; i<list.size(); i++){
            list.get(i).setRank(i+1);
        }

        return list;
    }

    public String suggestASolution(List<String> sols){
        List<WordScoreObject> listOfWordScoreObjs;

        listOfWordScoreObjs = getWordScoreObjectsWithoutRepetitiveCharacters(sols);
        if(listOfWordScoreObjs.size()>0){
            return listOfWordScoreObjs.get(0).getWord();
        }

        listOfWordScoreObjs = getAllWordScoreObjects(sols);
        if(listOfWordScoreObjs.size()>0){
            return listOfWordScoreObjs.get(0).getWord();
        }

        //no solution
        return null;
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

    private double computeScoreOfWord(String word){
        double score=0;
        for(int i=0; i<word.length(); i++){
            score+=charPercentage.get(word.charAt(i));
        }
        return score;
    }
}
