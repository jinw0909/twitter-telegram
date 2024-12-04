package io.blocksquare.twitterapi.app.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class ReferencedUser {

    @Id
    private String id;

    private String name;

    private String username;

    private String profilePic;
}
