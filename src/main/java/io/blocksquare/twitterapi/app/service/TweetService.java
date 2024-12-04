package io.blocksquare.twitterapi.app.service;

import com.twitter.clientlib.model.Get2UsersIdTimelinesReverseChronologicalResponse;
import com.twitter.clientlib.model.Get2UsersIdTweetsResponse;
import io.blocksquare.twitterapi.app.domain.User;

import java.util.List;

public interface TweetService {
    public List<Get2UsersIdTweetsResponse> getTweets(String[] userIds);
    public User register(String username);
}
