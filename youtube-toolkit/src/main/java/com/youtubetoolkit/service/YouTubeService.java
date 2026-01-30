package com.youtubetoolkit.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class YouTubeService {

    private final YouTube youtube;

    @Value("${youtube.api.key}")
    private String apiKey;

    /**
     * Extract video ID from YouTube URL
     */
    private static final Pattern VIDEO_ID_PATTERN =
            Pattern.compile(
                    "(?:v=|\\/)([0-9A-Za-z_-]{11})(?:\\?|&|$)"
            );

    public String extractVideoId(String input) {
        if (input == null || input.isBlank()) return null;

        // Direct ID
        if (input.matches("^[0-9A-Za-z_-]{11}$")) {
            return input;
        }

        Matcher matcher = VIDEO_ID_PATTERN.matcher(input);
        return matcher.find() ? matcher.group(1) : null;
    }


    /**
     * Extract channel ID from YouTube URL
     */
    private static final Pattern CHANNEL_ID_PATTERN =
            Pattern.compile("(UC[0-9A-Za-z_-]{22})");

    private static final Pattern HANDLE_PATTERN =
            Pattern.compile("@([A-Za-z0-9._-]+)");

    public String extractChannelId(String input) {
        if (input == null || input.isBlank()) return null;

        // Direct UC ID
        if (input.startsWith("UC") && input.length() == 24) {
            return input;
        }

        // From URL
        Matcher ucMatcher = CHANNEL_ID_PATTERN.matcher(input);
        if (ucMatcher.find()) {
            return ucMatcher.group(1);
        }

        // From @handle → needs API lookup later
        Matcher handleMatcher = HANDLE_PATTERN.matcher(input);
        if (handleMatcher.find()) {
            return handleMatcher.group(1); // return handle for API resolution
        }

        return null;
    }

    /**
     * Get channel ID from username
     */
    private String getChannelIdFromUsername(String username) {
        try {
            SearchListResponse response = youtube.search()
                    .list(List.of("snippet"))
                    .setQ(username.startsWith("@") ? username : "@" + username)
                    .setType(List.of("channel"))
                    .setMaxResults(1L)
                    .setKey(apiKey)
                    .execute();

            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                return null;
            }

            return response.getItems()
                    .get(0)
                    .getSnippet()
                    .getChannelId();

        } catch (Exception e) {
            log.error("Error resolving channel handle: {}", e.getMessage());
            return null;
        }
    }

    public String resolveChannelId(String input) {

        // 1. Direct UC ID
        if (input.startsWith("UC") && input.length() == 24) {
            return input;
        }

        // 2. Extract UC ID from URL
        Matcher ucMatcher = CHANNEL_ID_PATTERN.matcher(input);
        if (ucMatcher.find()) {
            return ucMatcher.group(1);
        }

        // 3. Resolve handle
        if (input.contains("@")) {
            String resolved = getChannelIdFromUsername(input);
            if (resolved != null) {
                return resolved;
            }
        }

        throw new IllegalArgumentException("Invalid or unsupported YouTube channel input");
    }


    /**
     * Get video details
     */
    public Video getVideoDetails(String videoId) {
        try {
            YouTube.Videos.List request = youtube.videos()
                    .list(List.of("snippet", "statistics", "contentDetails", "status"))
                    .setId(List.of(videoId))
                    .setKey(apiKey);

            VideoListResponse response = request.execute();

            if (response.getItems().isEmpty()) {
                throw new IllegalArgumentException("Video not found");
            }
            return response.getItems().get(0);
        } catch (Exception e) {
            log.error("Error fetching video details: {}", e.getMessage());
            throw new RuntimeException("Failed to fetch video details: " + e.getMessage());
        }
    }

    /**
     * Get channel details
     */
    public Channel getChannelDetails(String channelId) {
        try {
            ChannelListResponse response = youtube.channels()
                    .list(List.of("snippet", "statistics", "brandingSettings", "contentDetails", "status"))
                    .setId(List.of(channelId))
                    .setKey(apiKey)
                    .execute();

            if (response == null || response.getItems() == null || response.getItems().isEmpty()) {
                throw new IllegalArgumentException("Channel not found or inaccessible");
            }

            return response.getItems().get(0);

        } catch (Exception e) {
            log.error("Error fetching channel details", e);
            throw new RuntimeException("Failed to fetch channel details");
        }
    }

}