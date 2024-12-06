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

    public void sendMessageWithButton(String message, String tweetUrl) {
        try {
            // Create the inline keyboard JSON (raw JSON format, not URL-encoded)
            String inlineKeyboardJson = String.format(
                    "{\"inline_keyboard\": [[{\"text\": \"View Tweet\", \"url\": \"%s\"}]]}",
//                    "{\"inline_keyboard\": [[{\"text\": \"View Tweet\", \"web_app\": {\"url\": \"%s\"}}]]}",
                    tweetUrl
            );
            // Create the JSON body for the POST request
            String requestBody = String.format(
                    "{\"chat_id\": \"%s\", \"text\": \"%s\", \"disable_web_page_preview\": %b, \"parse_mode\": \"HTML\", \"reply_markup\": %s}",
                    chatId,
                    message,  // You can directly use the message, don't URL encode it
                    disablePreview,
                    inlineKeyboardJson  // Raw JSON string for the reply_markup
            );

            // Create HTTP client
            HttpClient client = HttpClient.newHttpClient();

            // Build the POST request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.telegram.org/bot" + botToken + "/sendMessage"))
                    .header("Content-Type", "application/json")  // Set content type to JSON
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))  // Set the JSON body
                    .build();

            // Send the request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response: " + response.body());

        } catch (Exception e) {
            log.error("Failed to send message with inline button", e);
        }
    }

    public void sendImagesWithButton(String caption, List<String> mediaUrls, String buttonUrl) {
        if (mediaUrls == null || mediaUrls.isEmpty()) {
            log.warn("No media URLs to send.");
            return;
        }

        try {
            // Step 1: Send the media group
            String url = String.format("https://api.telegram.org/bot%s/sendMediaGroup", botToken);

            StringBuilder mediaGroupBuilder = new StringBuilder("[");
            for (int i = 0; i < mediaUrls.size(); i++) {
                String mediaUrl = mediaUrls.get(i);
                if (i > 0) {
                    mediaGroupBuilder.append(",");
                }
                // Subsequent media items do not have a caption
                mediaGroupBuilder.append(String.format(
                        "{\"type\":\"photo\",\"media\":\"%s\"}",
                        mediaUrl
                ));
            }
            mediaGroupBuilder.append("]");

            String requestBody = String.format(
                    "{\"chat_id\":\"%s\",\"media\":%s}",
                    chatId,
                    mediaGroupBuilder.toString()
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> mediaGroupResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("Response (Media Group): {}", mediaGroupResponse.body());

            // Step 2: Send a separate message with the button
            sendMessageWithButton(caption, buttonUrl);

        } catch (Exception e) {
            log.error("Failed to send images with button from TelegramMessageSender", e);
        }
    }




}
