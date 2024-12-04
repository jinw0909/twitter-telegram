package io.blocksquare.twitterapi.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class TelegramMessageSender {

    private final String botToken;
    private final String chatId;

    public TelegramMessageSender(@Value("${telegram.bot.token}") String botToken, @Value("${telegram.chat.id}") String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
    }

    private boolean disablePreview = true;
    //Method to send a message
    public void sendMessage(String message) {
        try {
            //Encode the message
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s&disable_web_page_preview=%b&parse_mode=HTML",
                    botToken,
                    chatId,
                    encodedMessage,
                    disablePreview
            );

            //Create HTTP client
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();

            //Send request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response: " + response.body());
        } catch (Exception e) {
            log.error("failed to send message from TelegramMessageSender", e);
            e.printStackTrace();
        }
    }

    public void sendImages(String caption, List<String> mediaUrls) {

        if (mediaUrls == null || mediaUrls.isEmpty()) {
            log.warn("No media URLs to send.");
            return;
        }

        try {
            // Create the URL for sendMediaGroup
            String url = String.format("https://api.telegram.org/bot%s/sendMediaGroup", botToken);

            // Build the media group JSON array
            StringBuilder mediaGroupBuilder = new StringBuilder("[");
            for (int i = 0; i < mediaUrls.size(); i++) {
                String mediaUrl = mediaUrls.get(i);
                if (i > 0) {
                    mediaGroupBuilder.append(",");
                }
                if (i == 0) {
                    // Attach caption to the first media item
                    mediaGroupBuilder.append(String.format(
                            "{\"type\":\"photo\",\"media\":\"%s\",\"caption\":\"%s\",\"parse_mode\":\"HTML\"}",
                            mediaUrl,
                            caption.replace("\"", "\\\"") // Escape quotes in caption
                    ));
                } else {
                    // Subsequent media items do not have a caption
                    mediaGroupBuilder.append(String.format(
                            "{\"type\":\"photo\",\"media\":\"%s\"}",
                            mediaUrl
                    ));
                }
            }
            mediaGroupBuilder.append("]");

            // Create the request body
            String requestBody = String.format(
                    "{\"chat_id\":\"%s\",\"media\":%s}",
                    chatId,
                    mediaGroupBuilder.toString()
            );

            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            // Send request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response (Images with Caption): {}", response.body());
        } catch (Exception e) {
            log.error("Failed to send images with caption from TelegramMessageSender", e);
        }
    }



}
