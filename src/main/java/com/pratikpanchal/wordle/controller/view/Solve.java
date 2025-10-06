package com.pratikpanchal.wordle.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Solve {
    @GetMapping("/new")
    public String solveNew(Model model) {
        return "index2";
    }

    @GetMapping("/")
    public String solve(Model model) {
        return "index";
    }
}
