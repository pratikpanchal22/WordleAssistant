//package com.example.idea;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class InputGrid {

    static final int CHAR_ARRAY_IDX = 0;
    static final int COLOR_ARRAY_IDX = 1;

    List<char[][]> grid;

    public InputGrid() {
        grid = new ArrayList<>();
    }

    public int addRow(@NotNull String characters, @NotNull String colors){

        characters = characters.trim().toLowerCase();
        colors = colors.trim().toUpperCase();

        if(characters.length()!=5 || colors.length()!=5){
            throw new IllegalArgumentException("Input doesn't match the expected length");
        }

        char[][] row = new char[2][5];

        row[CHAR_ARRAY_IDX] = characters.toCharArray();
        row[COLOR_ARRAY_IDX] = colors.toCharArray();
        this.grid.add(row);

        return this.grid.size()-1;
    }

}
