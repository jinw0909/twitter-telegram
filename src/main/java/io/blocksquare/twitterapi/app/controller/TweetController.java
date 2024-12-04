package io.blocksquare.twitterapi.app.controller;

import com.twitter.clientlib.model.Get2UsersIdTweetsResponse;
import io.blocksquare.twitterapi.app.domain.User;
import io.blocksquare.twitterapi.app.service.TweetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TweetController {

    private final TweetService tweetService;

    @GetMapping("/tweets")
    public List<Get2UsersIdTweetsResponse> getTweets() {
        String[] userIds = {"44196397", "26538229"};
        log.info("executing fetch tweets");
        return tweetService.getTweets(userIds);
    }

    @GetMapping("/register/{username}")
    public User register(@PathVariable String username) {
        User user = tweetService.register(username);
        return user;
    }

}
