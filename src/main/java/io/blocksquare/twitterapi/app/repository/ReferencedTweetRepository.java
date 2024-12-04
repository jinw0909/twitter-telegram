package io.blocksquare.twitterapi.app.repository;

import io.blocksquare.twitterapi.app.domain.ReferencedTweet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferencedTweetRepository extends JpaRepository<ReferencedTweet, String> {
}
