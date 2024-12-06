package io.blocksquare.twitterapi.app.repository;

import io.blocksquare.twitterapi.app.domain.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, String> {
}
