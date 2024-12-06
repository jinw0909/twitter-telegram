package io.blocksquare.twitterapi.app.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter @Setter
public class User {

    @Id
    private String id;

    private String name;

    private String username;

    private String profilePic;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Tweet> tweets;

}
