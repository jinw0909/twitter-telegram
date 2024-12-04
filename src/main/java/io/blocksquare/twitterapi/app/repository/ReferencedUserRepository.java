package io.blocksquare.twitterapi.app.repository;

import io.blocksquare.twitterapi.app.domain.ReferencedUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferencedUserRepository extends JpaRepository<ReferencedUser, String> {
}
