package io.blocksquare.twitterapi.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Media {

    @Id
    @Column(name = "media_key", nullable = false, unique = true)
    private String mediaKey;

    @ManyToOne
    @JoinColumn(name = "tweet_id", nullable = false)
    private Tweet tweet;

    @Column(name = "type", nullable = true)
    private String type;

    @Column(name = "url", nullable = true, columnDefinition = "TEXT")
    private String url;

    @Column(name = "preview_image_url", nullable = true, columnDefinition = "TEXT")
    private String previewImageUrl;

}
