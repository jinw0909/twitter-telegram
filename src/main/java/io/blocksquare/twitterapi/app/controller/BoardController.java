package io.blocksquare.twitterapi.app.controller;

import io.blocksquare.twitterapi.app.domain.Tweet;
import io.blocksquare.twitterapi.app.domain.User;
import io.blocksquare.twitterapi.app.service.TweetService;
import io.blocksquare.twitterapi.app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final TweetService tweetService;
    private final UserService userService;

    @GetMapping("/board")
    public String showBoard(Model model) {

        List<User> allUsers = userService.getAllUsers();
        List<Tweet> tweets = tweetService.getAllTweets();

        tweets.sort(Comparator.comparing(Tweet::getCreatedAt).reversed());

        model.addAttribute("users", allUsers);
        model.addAttribute("tweets", tweets);

        return "board";
    }
}
