package tools;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
            System.out.println ("Importing: " + importCounter + " word:"+ strLine);
            words.add(strLine.trim());
        }

        //Close the input stream
        try {
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return words;

    }
}
