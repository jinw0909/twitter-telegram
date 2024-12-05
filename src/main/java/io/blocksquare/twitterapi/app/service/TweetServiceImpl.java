package io.blocksquare.twitterapi.app.service;

import com.twitter.clientlib.ApiException;
import com.twitter.clientlib.JSON;
import com.twitter.clientlib.TwitterCredentialsBearer;
import com.twitter.clientlib.api.TweetsApi;
import com.twitter.clientlib.api.TwitterApi;
import com.twitter.clientlib.model.*;
import io.blocksquare.twitterapi.app.TelegramMessageSender;
import io.blocksquare.twitterapi.app.domain.ReferencedTweet;
import io.blocksquare.twitterapi.app.domain.ReferencedUser;
import io.blocksquare.twitterapi.app.domain.User;
import io.blocksquare.twitterapi.app.repository.ReferencedTweetRepository;
import io.blocksquare.twitterapi.app.repository.ReferencedUserRepository;
import io.blocksquare.twitterapi.app.repository.TweetRepository;
import io.blocksquare.twitterapi.app.repository.UserRepository;
import io.blocksquare.twitterapi.config.ExtendedMedia;
import io.swagger.annotations.Api;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TweetServiceImpl implements TweetService {

    private TwitterApi apiInstance;
    private final TelegramMessageSender telegramSender;
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final ReferencedUserRepository referencedUserRepository;
    private final ReferencedTweetRepository referencedTweetRepository;

    @Value("${twitter.app-only-access-token}")
    private String APP_ONLY_ACCESS_TOKEN;

    @PostConstruct
    public void initTwitterApiInstance() {
        TwitterCredentialsBearer credentials = new TwitterCredentialsBearer(APP_ONLY_ACCESS_TOKEN);
        apiInstance = new TwitterApi(credentials);
    }

    @Override
    public List<Get2UsersIdTweetsResponse> getTweets(String[] userIds) {
        //String id = "44196397";
        Integer maxResults = 5;
        Set<String> exclude = new HashSet<>(List.of("replies", "retweets"));
//        Set<String> tweetFields = new HashSet<>(List.of("id", "referenced_tweets", "created_at"));
        Set<String> tweetFields = new HashSet<>(List.of("id", "created_at", "text", "entities"));
        Set<String> mediaFields = new HashSet<>(List.of("preview_image_url", "url", "type"));
        Set<String> expansions = new HashSet<>(List.of("author_id", "referenced_tweets.id", "referenced_tweets.id.author_id", "attachments.media_keys"));
        List<Get2UsersIdTweetsResponse> results = new ArrayList<>();
        for (String userId : userIds) {
            try {
                String username = userRepository.getUsernameById(userId);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found for ID: " + userId));
                String sinceId = tweetRepository.findMostRecentTweetId(userId);
//                Set<String> userField = new HashSet<>(List.of("most_recent_tweet_id"));
//                Get2UsersIdResponse foundUser = apiInstance.users().findUserById(userId).userFields(userField).execute();
//                log.info("foundUser: {}", foundUser);
                //sinceId = foundUser.getData().getMostRecentTweetId();
                log.info("sinceId of {} is {}", username, sinceId);

//                Get2UsersIdTweetsResponse result = apiInstance.tweets().usersIdTweets(userId).exclude(exclude).maxResults(maxResults)
//                        .tweetFields(tweetFields).sinceId(sinceId).mediaFields(mediaFields).expansions(expansions).execute();

                Get2UsersIdTweetsResponse result = apiInstance.tweets().usersIdTweets(userId).exclude(exclude).maxResults(maxResults)
                        .tweetFields(tweetFields).mediaFields(mediaFields).expansions(expansions).execute();


                List<Tweet> rawTweets = result.getData();

                //log.info("fetch data = {}", rawTweets);
                if (rawTweets != null && !rawTweets.isEmpty()) {
                    Collections.reverse(rawTweets);
                    rawTweets.forEach(rawTweet -> {
                        String message = rawTweet.getText();

                        // Slice the message only if referencedTweets is not null
                        if (rawTweet.getReferencedTweets() != null && !rawTweet.getReferencedTweets().isEmpty() && rawTweet.getReferencedTweets().get(0).getType().toString().equals("quoted")) {
                            int urlIndex = message.lastIndexOf("https://");
                            if (urlIndex != -1) {
                                message = message.substring(0, urlIndex).trim();
                            }
                        }

                        String tweetUrl = String.format("https://twitter.com/%s/status/%s", username, rawTweet.getId());
                        io.blocksquare.twitterapi.app.domain.Tweet tweet = new io.blocksquare.twitterapi.app.domain.Tweet();
                        tweet.setId(rawTweet.getId());
                        tweet.setText(message);
                        tweet.setCreatedAt(rawTweet.getCreatedAt().toInstant());
                        tweet.setTweetUrl(tweetUrl);
                        tweet.setAuthor(user);

                        // Check if referencedTweets exists and is not empty
                        if (rawTweet.getReferencedTweets() != null && !rawTweet.getReferencedTweets().isEmpty()) {
                            tweet.setReferencedTweetId(rawTweet.getReferencedTweets().get(0).getId());
                        } else {
                            tweet.setReferencedTweetId(null); // Explicitly set to null if no referenced tweets
                        }

                        // Extract the media key
                        List<String> mediaUrls = new ArrayList<>(); // Holds either previewImageUrl or photo URL
                        if (rawTweet.getAttachments() != null && rawTweet.getAttachments().getMediaKeys() != null) {
                            List<String> mediaKeys = rawTweet.getAttachments().getMediaKeys();

                            // Match the media key with the corresponding media object in includes.media
                            if (result.getIncludes() != null && result.getIncludes().getMedia() != null) {
                                for (String mediaKey : mediaKeys) {
                                    for (Media media : result.getIncludes().getMedia()) {
                                        if (mediaKey.equals(media.getMediaKey())) {
                                            if ("video".equals(media.getType()) && media instanceof Video video) {
                                                if (video.getPreviewImageUrl() != null) {
                                                    mediaUrls.add(video.getPreviewImageUrl().toString());
                                                }
                                            } else if ("photo".equals(media.getType()) && media instanceof Photo photo) {
                                                if (photo.getUrl() != null) {
                                                    mediaUrls.add(photo.getUrl().toString());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Send message to Telegram
                        if (!mediaUrls.isEmpty()) {
//                            String caption = String.format("**%s**\n%s\n\n%s", user.getName(), message, tweetUrl);
//                            String caption = "<b>" + user.getName() + "</b>\n" + message + "\n\n" + "<i>" + tweetUrl + "</i>";
                            String caption = formatHtmlCaption(user.getName(), message, tweetUrl);
                            log.info("Sending image to Telegram: caption={}, mediaUrl={}", caption, mediaUrls);
                            telegramSender.sendImages(caption, mediaUrls); // Send image with caption
                        } else {
//                            String formattedMessage = String.format("**%s**\n%s\n\n%s", user.getName(), message, tweetUrl);
//                            String formattedMessage = "<b>" + user.getName() + "</b>\n" + message + "\n\n" + "<i>" + tweetUrl + "</i>";
                            String formattedMessage = formatHtmlCaption(user.getName(), message, tweetUrl);
                            log.info("Sending text to Telegram: {}", formattedMessage);
                            telegramSender.sendMessageWithButton(formattedMessage, tweetUrl); // Send plain text
                        }

                        tweetRepository.save(tweet);

                    });


                    results.add(result);
                } else {
                    log.warn("No tweets found for user ID: {}", userId);
                }

                if (result.getIncludes() != null && result.getIncludes().getTweets() != null) {

                    List<Tweet> rawRefTweets = result.getIncludes().getTweets();

                    // Process rawRefTweets
                    rawRefTweets.forEach(tweet -> {
                        try {
                            Set<String> userFields = new HashSet<>(List.of("profile_image_url"));

                            Get2UsersIdResponse rawUser = apiInstance.users().findUserById(tweet.getAuthorId()).userFields(userFields).execute();

                            ReferencedUser refUser = new ReferencedUser();
                            refUser.setName(rawUser.getData().getName());
                            refUser.setUsername(rawUser.getData().getUsername());
                            refUser.setId(rawUser.getData().getId());
                            refUser.setProfilePic(rawUser.getData().getProfileImageUrl().toString());
                            ReferencedUser savedRefUser = referencedUserRepository.save(refUser);
                            log.info("savedRefUser = {}", savedRefUser);

                            ReferencedTweet refTweet = new ReferencedTweet();
                            String refTweetUrl = String.format("https://twitter.com/%s/status/%s", rawUser.getData().getUsername(), tweet.getId());
                            refTweet.setTweetUrl(refTweetUrl);
                            refTweet.setId(tweet.getId());
                            refTweet.setText(tweet.getText());
                            refTweet.setCreatedAt(tweet.getCreatedAt().toInstant());
                            refTweet.setAuthor(savedRefUser);

                            ReferencedTweet savedRefTweet = referencedTweetRepository.save(refTweet);
                            log.info("saved ref tweet = {}", savedRefTweet);
                        } catch (ApiException e) {
                            log.error("failed to find the referenced user", e);
                        }
                    });

                } else {
                    System.out.println("No referenced tweets to process.");
                }

            } catch (ApiException e) {
                log.error("Exception when calling TweetsApi#usersIdTweets", e);
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    public User register(String username) {

        Set<String> userFields = new HashSet<>(List.of("profile_image_url"));

        try {
            Get2UsersByUsernameUsernameResponse userInfo = apiInstance.users().findUserByUsername(username).userFields(userFields).execute();
            log.info("userInfo = {}", userInfo);
            User user = new User();
            user.setId(userInfo.getData().getId());
            user.setName(userInfo.getData().getName());
            user.setUsername(userInfo.getData().getUsername());
            user.setProfilePic(userInfo.getData().getProfileImageUrl().toString());
            User savedUser = userRepository.save(user);
            return savedUser;
        } catch (ApiException e) {
            log.error("cannot find user", e);
        }

        return null;
    }

    @Override
    public List<io.blocksquare.twitterapi.app.domain.Tweet> getTweetsByAuthorId(String authorId) {
        return tweetRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }

    private String formatHtmlCaption(String name, String message, String url) {
        return String.format(
                "<b>%s\n\n</b>%s\n\n<i>%s</i>",
                escapeHtml(name),
                escapeHtml(message),
                escapeHtml(url)
        );
    }

    @Override
    public List<io.blocksquare.twitterapi.app.domain.Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
