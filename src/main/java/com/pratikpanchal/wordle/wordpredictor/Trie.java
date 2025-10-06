package com.pratikpanchal.wordle.wordpredictor;//package com.example.idea;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Trie {

    public static TrieNode addWordsToTrie(@NotNull List<String> words) {
        TrieNode root = new TrieNode();
        for (String word : words) {
            addWord(word, root);
        }

        return root;
    }

    private static void addWord(String word, TrieNode n) {

        TrieNode runner = n;
        for (int i = 0; i < word.length(); i++) {
            int idx = word.charAt(i) - 'a';
            if (runner.next[idx] == null) {
                runner.next[idx] = new TrieNode();
            }
            runner = runner.next[idx];
            runner.c = word.charAt(i);
            runner.numberOfBranches++;
        }
        runner.endsHere = true;
    }
}
