package service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Instance-based service for Telegram notifications.
 */
public class TelegramService {
    private final String botToken;
    private final String chatId;

    public TelegramService(String botToken, String chatId) {
        this.botToken = botToken;
        this.chatId = chatId;
        System.out.println(botToken);
        System.out.println(chatId);
    }

    /**
     * Sends an asynchronous message to the configured Telegram chat.
     * @param message The text content to transmit.
     */
    public void sendNotification(String message) {
        // Basic validation to avoid empty requests
        if (botToken == null || botToken.isEmpty() || chatId == null || chatId.isEmpty()) {
            System.out.println("[Telegram] Error: Missing Token or Chat ID.");
            return;
        }

        try {
            //Parameters must be encoded for application/x-www-form-urlencoded
            String encodedChatId = URLEncoder.encode(chatId, StandardCharsets.UTF_8);
            String encodedMsg = URLEncoder.encode(message, StandardCharsets.UTF_8);

            //Build the POST body
            String requestBody = "chat_id=" + encodedChatId + "&text=" + encodedMsg;

            //Construct the clean URL (Token is part of the path)
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";

            //Force HTTP/1.1 to bypass some picky Nginx configurations
            HttpClient client = HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_1_1)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            System.out.println("[Telegram] Attempting to send notification...");

            // Async execution to prevent UI freezing
            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        // Debugging the response from Telegram
                        System.out.println("[Telegram API] Status: " + response.statusCode());
                        System.out.println("[Telegram API] Body: " + response.body());
                    })
                    .exceptionally(ex -> {
                        System.err.println("[Telegram API] Connection Failed: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("[Telegram API] Execution Error: " + e.getMessage());
        }
    }
}