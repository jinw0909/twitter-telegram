package io.blocksquare.twitterapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TwitterApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterApiApplication.class, args);
    }

}
