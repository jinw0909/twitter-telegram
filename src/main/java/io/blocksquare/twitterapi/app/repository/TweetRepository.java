package io.blocksquare.twitterapi.app.repository;

import io.blocksquare.twitterapi.app.domain.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface TweetRepository extends JpaRepository<Tweet, String> {

    @Query(value = "SELECT t.id FROM tweet t WHERE t.author_id = :userId ORDER BY t.created_at DESC LIMIT 1", nativeQuery = true)
    String findMostRecentTweetId(@Param("userId") String userId);

}
