package com.pratikpanchal.wordle.tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class WordImporter {

    public List<String> importWords(String file){
        // Open the file
        FileInputStream fstream = null;

        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine = null;
        List<String> words = new ArrayList<>();
        int importCounter=0;

        //Read File Line By Line
        while (true)   {
            try {
                if (!((strLine = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Print the content on the console
            ++importCounter;
            //System.out.println ("Importing: " + importCounter + " word:"+ strLine);
            words.add(strLine.trim());
        }

        //Close the input stream
        try {
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Imported "+ importCounter + " words");
        return words;
    }

    public List<String> importWords(String file, Set<String> exclusions){
        // Open the file
        FileInputStream fstream = null;

        try {
            fstream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
        String strLine = null;
        List<String> words = new ArrayList<>();
        int importCounter=0;

        //Read File Line By Line
        while (true)   {
            try {
                if (!((strLine = br.readLine()) != null)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Print the content on the console
            ++importCounter;
            //System.out.println ("Importing: " + importCounter + " word:"+ strLine);
            String word = strLine.trim();
            if(!exclusions.contains(word)){
                words.add(strLine.trim());
            }
        }

        //Close the input stream
        try {
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.printf("Imported "+ importCounter + " words");
        return words;
    }
}
