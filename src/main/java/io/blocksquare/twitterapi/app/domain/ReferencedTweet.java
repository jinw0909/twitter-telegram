package io.blocksquare.twitterapi.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class ReferencedTweet {

    @Id
    private String id;

    @Column(columnDefinition = "TEXT")
    private String text;

    private Instant createdAt;

    private String tweetUrl;

    @ManyToOne
    private ReferencedUser author; //Author of the referenced tweet
}
