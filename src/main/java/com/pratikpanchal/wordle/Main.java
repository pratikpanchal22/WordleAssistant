package com.pratikpanchal.wordle;//package com.example.idea;
import org.apache.commons.logging.LogFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static final Logger log = LogManager.getLogger(Main.class);
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        log.info("WordleAssistant is ready!");
    }
}
