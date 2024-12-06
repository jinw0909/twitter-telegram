package io.blocksquare.twitterapi.scheduler;

import io.blocksquare.twitterapi.app.service.TweetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MyScheduledTask {

    private final TweetService tweetService;

    @Scheduled(fixedRate = 300000)
    public void runTask() {
        System.out.println("Task executed at: " + System.currentTimeMillis());
    }

//    @Scheduled(fixedRate = 900000) //Runs every 10 minutues
//    public void fetchTweets() {
//        try {
//            log.info("Starting scheduled task to fetch tweets");
//            String[] userIds = {"44196397", "26538229"};
//            tweetService.getTweets(userIds); // Use the interface method
//            log.info("Finished fetching tweets.");
//        } catch (Exception e) {
//            log.error("Error occurred while fetching tweets during scheduled task.", e);
//        }
//    }
}
