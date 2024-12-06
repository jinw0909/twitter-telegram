package io.blocksquare.twitterapi.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "tweet")
@Getter @Setter
public class Tweet {

    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;

    @Column(name = "text", nullable = true, columnDefinition = "TEXT")
    private String text;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "tweet_url", nullable = false)
    private String tweetUrl;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author; //Author of the referenced tweet

    @Column(name = "referenced_tweet_id", nullable = true)
    private String referencedTweetId;

    @OneToMany(mappedBy = "tweet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> media;

}
