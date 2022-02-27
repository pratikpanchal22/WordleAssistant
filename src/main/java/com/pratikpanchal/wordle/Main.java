package com.pratikpanchal.wordle;//package com.example.idea;

import com.pratikpanchal.wordle.hintprovider.HintProvider;
import com.pratikpanchal.wordle.tools.WordImporter;
import com.pratikpanchal.wordle.wordpredictor.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.*;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
