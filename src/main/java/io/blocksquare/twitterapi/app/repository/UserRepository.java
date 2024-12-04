package io.blocksquare.twitterapi.app.repository;

import io.blocksquare.twitterapi.app.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    @Query("SELECT u.username FROM User u WHERE u.id = :id")
    String getUsernameById(@Param("id") String id);
}
