//package com.example.idea;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Trie {

    public static Node addWordsToTrie(@NotNull List<String> words){
        Node root = new Node();
        for(String word : words){
            addWord(word, root);
        }

        Queue<Node> q = new LinkedList<>();
        q.add(root);
        int level=0;
        while(q.size()>0){
            int size = q.size();
            int numOfBranches=0;
            int[] countMap = new int[26];
            while(size-- > 0){
                Node n = q.remove();
                if(n==null){
                    continue;
                }
                numOfBranches+= n.numberOfBranches;

                //add all next nodes to queue
                for(Node nxt : n.next) if(nxt!=null){
                    countMap[nxt.c-'a']++;
                    q.add(nxt);
                }
            }
            System.out.println("Level: "+level + " number of branches:"+ numOfBranches);
            System.out.println("CountMap:"+ Arrays.toString(countMap));
            ++level;
        }

        return root;
    }

    private static void addWord(String word, Node n){

        Node runner = n;
        for(int i=0; i<word.length(); i++){

            int idx = word.charAt(i) - 'a';
            if(runner.next[idx]==null){
                runner.next[idx]=new Node();
            }
            runner = runner.next[idx];

            runner.c = word.charAt(i);
            runner.numberOfBranches++;

        }
        runner.endsHere=true;
    }
}
