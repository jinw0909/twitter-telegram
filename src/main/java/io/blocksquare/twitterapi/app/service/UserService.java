package io.blocksquare.twitterapi.app.service;

import io.blocksquare.twitterapi.app.domain.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
}
