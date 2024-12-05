package io.blocksquare.twitterapi.app.service;

import io.blocksquare.twitterapi.app.domain.User;
import io.blocksquare.twitterapi.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        log.info("allUsers: {}", users);
        return users;
    }
}
