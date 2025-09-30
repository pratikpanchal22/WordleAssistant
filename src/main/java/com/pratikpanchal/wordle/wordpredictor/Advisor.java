package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class Advisor {

    private static final int ALPHABET_SIZE = 26;
    private static final Comparator<WordScoreObject> ATTEMPT5_COMPARATOR =
            Comparator.comparing(WordScoreObject::getScore, Comparator.nullsLast(Double::compare))
                    .thenComparing(WordScoreObject::getPositionalScore,
                            Comparator.nullsLast(Double::compare))
                    .thenComparing(WordScoreObject::getUniqueCharacterCount,
                            Comparator.nullsLast(Integer::compare))
                    .thenComparing(WordScoreObject::getRepeatCharacterCount,
                            Comparator.nullsLast(Integer::compare).reversed())
                    .reversed();
    private static final Comparator<WordScoreObject> ATTEMPT6_COMPARATOR =
            Comparator.comparing(WordScoreObject::getPositionalScore,
                            Comparator.nullsLast(Double::compare))
                    .thenComparing(WordScoreObject::getScore, Comparator.nullsLast(Double::compare))
                    .thenComparing(WordScoreObject::getUniqueCharacterCount,
                            Comparator.nullsLast(Integer::compare))
                    .thenComparing(WordScoreObject::getRepeatCharacterCount,
                            Comparator.nullsLast(Integer::compare).reversed())
                    .reversed();
    private static final Comparator<WordScoreObject> DEFAULT_COMPARATOR =
            Comparator.comparing(WordScoreObject::getUniqueCharacterCount,
                            Comparator.nullsLast(Integer::compare))
                    .thenComparing(WordScoreObject::getRepeatCharacterCount,
                            Comparator.nullsLast(Integer::compare))
                    .thenComparing(WordScoreObject::getPositionalScore,
                            Comparator.nullsLast(Double::compare))
                    .thenComparing(WordScoreObject::getScore, Comparator.nullsLast(Double::compare))
                    .reversed();
    private final Logger log = LogManager.getLogger(this.getClass());
    private final List<String> words;
    private final Map<Character, Double> charPercentage;
    private final Map<String, Double> positionalScore = new HashMap<>();
    private final Map<String, Double> wordScoreMap;
    private Map<Character, Double>[] positionalCharScoreMap;

    public Advisor(List<String> words) {
        // Defensive copy & null safety
        if (words == null) {
            words = new ArrayList<>();
        }

        // Ensure immutability of input
        this.words = List.copyOf(words);
        this.charPercentage = new HashMap<>();
        this.wordScoreMap = new HashMap<>();

        if (words.isEmpty()) {
            return;
        }

        // Validate all words have same length
        int wordLength = words.get(0).length();
        for (String word : words) {
            if (word.length() != wordLength) {
                throw new IllegalArgumentException("All words must have the same length");
            }
            if (!word.matches("[a-z]+")) {
                throw new IllegalArgumentException("Words must only contain lowercase letters " +
                        "a-z: " + word);
            }
        }

        // Compute frequency
        int[] frequency = new int[ALPHABET_SIZE];
        int totalChars = words.size() * wordLength;

        for (String word : words) {
            for (char c : word.toCharArray()) {
                frequency[c - 'a']++;
            }
        }

        // Calculate percentage distribution
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            if (frequency[i] > 0) {
                char letter = (char) (i + 'a');
                double percentage = (frequency[i] * 100.0) / totalChars;
                charPercentage.put(letter, percentage);
            }
        }

        // Pre-compute scores
        computeScoreOfWords(this.words);
        computePositionalScoreOfWords(this.words);
    }

    public Map<Character, Double>[] getPositionalCharScoreMap() {
        return positionalCharScoreMap;
    }

    public Map<Character, Double> getCharPercentage() {
        return charPercentage;
    }

    public List<String> getWordsWithAtleastKCharacterFrequency(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be greater than 0");
        }

        List<String> result = new ArrayList<>();

        for (String word : words) {
            if (word == null || word.isEmpty()) {
                continue;
            }
            Map<Character, Integer> frequencyMap = new HashMap<>();
            for (char c : word.toCharArray()) {
                int newCount = frequencyMap.getOrDefault(c, 0) + 1;
                frequencyMap.put(c, newCount);

                if (newCount >= k) {
                    result.add(word);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Returns the top {@code k} most frequently used characters across all words.
     *
     * @param k number of most frequent characters to return
     * @return list of characters ordered by descending frequency
     * @throws IllegalArgumentException if {@code k <= 0} or words is null/empty
     */
    public List<Character> getKMostUsedCharacters(int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be greater than 0");
        }
        if (words == null || words.isEmpty()) {
            throw new IllegalStateException("Word list must not be null or empty");
        }

        Map<Character, Integer> frequencyMap = new HashMap<>();
        int totalChars = 0;

        // Count character frequencies
        for (String word : words) {
            if (word == null) continue;
            for (char c : word.toCharArray()) {
                if (Character.isLetter(c)) { // defensive: only count letters
                    frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
                    totalChars++;
                }
            }
        }

        // Update charPercentage map
        charPercentage.clear();
        for (Map.Entry<Character, Integer> entry : frequencyMap.entrySet()) {
            double percentage = (entry.getValue() * 100.0) / totalChars;
            charPercentage.put(entry.getKey(), percentage);
        }

        // Build a max-heap based on frequency
        PriorityQueue<Map.Entry<Character, Integer>> pq = new PriorityQueue<>(
                (a, b) -> Integer.compare(b.getValue(), a.getValue())
        );
        pq.addAll(frequencyMap.entrySet());

        // Extract top-k characters
        List<Character> result = new ArrayList<>();
        for (int i = 0; i < k && !pq.isEmpty(); i++) {
            result.add(pq.poll().getKey());
        }

        return result;
    }

    /**
     * Builds and returns a ranked list of WordScoreObject instances.
     *
     * @param words   the list of words to evaluate
     * @param attempt optional sort configuration (default = 0)
     * @return a ranked, sorted, immutable list of WordScoreObjects
     * @throws IllegalArgumentException if words is null or empty
     */
    public List<WordScoreObject> getAllWordScoreObjects(List<String> words,
                                                        Integer attempt) {
        if (words == null || words.isEmpty()) {
            throw new IllegalArgumentException("Words list must not be null or empty");
        }

        List<WordScoreObject> list = new ArrayList<>(words.size());

        for (String word : words) {
            if (word == null) continue; // defensive check

            Double score = wordScoreMap.get(word);
            Double positional = positionalScore.get(word);

            // Default to 0.0 if missing to avoid NPE
            list.add(new WordScoreObject(
                    word,
                    score != null ? score : 0.0,
                    positional != null ? positional : 0.0
            ));
        }

        sortConfigForAttempt(attempt, list);

        // Populate rank based on final order
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }

        return Collections.unmodifiableList(list);
    }

    /**
     * Sorts the provided list of WordScoreObjects according to the attempt config.
     *
     * @param attemptNumber the sorting strategy to apply
     * @param list          the list to sort
     */
    private void sortConfigForAttempt(int attemptNumber, List<WordScoreObject> list) {
        Objects.requireNonNull(list, "Word list must not be null");

        Comparator<WordScoreObject> comparator = switch (attemptNumber) {
            case 5 -> ATTEMPT5_COMPARATOR;
            case 6 -> ATTEMPT6_COMPARATOR;
            default -> DEFAULT_COMPARATOR;
        };

        list.sort(comparator);
    }

    public List<WordScoreObject> getWordScoreObjectsWithoutRepetitiveCharacters1(List<String> words) {
        List<WordScoreObject> list = new ArrayList<>();
        for (String word : words)
            if (!wordHasRepetitiveCharacters(word)) {
                list.add(new WordScoreObject(word,
                        wordScoreMap.get(word),
                        positionalScore.get(word)));
            }

        //sort list
        list.sort((a, b) -> {
            //if(a.getScore()==b.getScore()){
            return Double.compare(b.getPositionalScore(), a.getPositionalScore());
            //}
            //return (Double.compare(b.getScore(), a.getScore()));
        });

        //Populate rank
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }

        return list;
    }

    public String suggestASolutionFromAllEquallyLikelySolutions(List<String> sols,
                                                                int attemptNumber) {
        List<WordScoreObject> wordScoreObjects = getAllWordScoreObjects(sols, attemptNumber);

        Map<String, List<String>> map = new HashMap<>();
        String filterStr = wordScoreObjects.get(0).getUniqueIdString();
        List<WordScoreObject> canList =
                wordScoreObjects.stream().filter(wso -> wso.getUniqueIdString().equals(filterStr)).collect(Collectors.toList());

        int randomPick = (int) (Math.random() * canList.size());
        return canList.get(randomPick).getWord().orElse(null);
    }

    /**
     * Given the list of all possible solutions (strings), and the attempt number,
     * suggests and returns the most probable solution based on the computational model
     *
     * @param sols:          list of all possible solutions
     * @param attemptNumber: attempt number that the suggested solutions is for
     * @return: String: a single word out of sols that is computationally fittest
     */
    public Optional<String> suggestASolution(List<String> sols, int attemptNumber) {
        List<WordScoreObject> listOfWordScoreObjs;

        if (sols.isEmpty()) {
            return Optional.empty();
        }

        listOfWordScoreObjs = getAllWordScoreObjects(sols, attemptNumber);
        if (!listOfWordScoreObjs.isEmpty()) {
            return listOfWordScoreObjs.get(0).getWord();
        }

        return Optional.empty();
    }

    public Double getCharScore(char c) {
        return charPercentage.getOrDefault(c, 0.0);
    }

    private boolean wordHasRepetitiveCharacters(String word) {
        HashSet<Character> set = new HashSet<>();
        for (char c : word.toCharArray()) {
            if (set.contains(c)) {
                return true;
            }
            set.add(c);
        }
        return false;
    }

    private void computeScoreOfWords(List<String> words) {
        for (String word : words) {
            wordScoreMap.put(word, computeCharFrequencyScoreOfWord(word));
        }
    }

    /**
     * Compute the positionalScore of the give words
     * positionalScore of a word is the sum of all the positionalScores of it's characters
     * positionalScore of the character at the given position is defined as:
     * freq / number of total words
     * where freq = number of times the character appears at that position
     *
     * @param words
     */
    private void computePositionalScoreOfWords(List<String> words) {
        /**
         * row represents the position/index
         * column represents the character
         */
        int[][] positionalCharFrequencies = new int[5][26];

        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                positionalCharFrequencies[i][word.charAt(i) - 'a']++;
            }
        }
        positionalCharScoreMap = new HashMap[5];
        for (int i = 0; i < positionalCharScoreMap.length; i++) {
            positionalCharScoreMap[i] = new HashMap<Character, Double>();
        }

        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 26; c++) {
                positionalCharScoreMap[r].put((char) (c + 'a'),
                        ((double) positionalCharFrequencies[r][c] / (double) words.size()));
            }
        }

        for (String word : words) {
            positionalScore.put(word, computePositionalScoreOfWord(word, positionalCharScoreMap));
        }
    }

    private double computePositionalScoreOfWord(String word,
                                                Map<Character, Double>[] positionalCharScoreMap) {
        double score = 0.0;
        for (int i = 0; i < word.length(); i++) {
            score += positionalCharScoreMap[i].get(word.charAt(i));
        }
        return score;
    }

    private double computeCharFrequencyScoreOfWord(String word) {
        double score = 0;
        for (int i = 0; i < word.length(); i++) {
            score += charPercentage.get(word.charAt(i));
        }
        return score;
    }
}
