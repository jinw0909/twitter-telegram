package io.blocksquare.twitterapi.app.service;

import com.twitter.clientlib.model.Get2UsersIdTimelinesReverseChronologicalResponse;
import com.twitter.clientlib.model.Get2UsersIdTweetsResponse;
import io.blocksquare.twitterapi.app.domain.Tweet;
import io.blocksquare.twitterapi.app.domain.User;

import java.util.List;

public interface TweetService {
    List<Get2UsersIdTweetsResponse> getTweets(String[] userIds);
    User register(String username);
    List<Tweet> getTweetsByAuthorId(String authorId);
    List<Tweet> getAllTweets();
}
