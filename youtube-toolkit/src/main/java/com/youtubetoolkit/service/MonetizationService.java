package com.youtubetoolkit.service;

import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonetizationService {

    private final YouTubeService youtubeService;

    /**
     * Check if a video is monetized
     */
    public boolean isVideoMonetized(String videoId) {
        try {
            Video video = youtubeService.getVideoDetails(videoId);

            boolean hasStandardLicense = "youtube".equals(video.getStatus().getLicense());
            boolean isEmbeddable = Boolean.TRUE.equals(video.getStatus().getEmbeddable());
            boolean isPublic = "public".equals(video.getStatus().getPrivacyStatus());
            boolean madeForKids = Boolean.TRUE.equals(video.getStatus().getMadeForKids());

            return hasStandardLicense && isEmbeddable && isPublic && !madeForKids;

        } catch (Exception e) {
            log.error("Error checking video monetization: {}", e.getMessage());
            throw new RuntimeException("Failed to check monetization status");
        }
    }

    /**
     * Check if a channel is monetized
     */
    public boolean isChannelMonetized(String channelId) {
        try {
            Channel channel = youtubeService.getChannelDetails(channelId);

            Long subscriberCount = channel.getStatistics().getSubscriberCount() != null
                    ? channel.getStatistics().getSubscriberCount().longValue() : 0L;

            Long videoCount = channel.getStatistics().getVideoCount() != null
                    ? channel.getStatistics().getVideoCount().longValue() : 0L;

            Long viewCount = channel.getStatistics().getViewCount() != null
                    ? channel.getStatistics().getViewCount().longValue() : 0L;

            return subscriberCount >= 1000 && viewCount >= 4000 && videoCount >= 10;

        } catch (Exception e) {
            log.error("Error checking channel monetization: {}", e.getMessage());
            throw new RuntimeException("Failed to check channel monetization status");
        }
    }
}
//package com.youtubetoolkit.service;
//
//import com.google.api.services.youtube.model.Channel;
//import com.google.api.services.youtube.model.Video;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class MonetizationService {
//
//    private final YouTubeService youtubeService;
//
//    /**
//     * Check if a video is monetized
//     * Note: YouTube API doesn't directly provide monetization status
//     * We use indicators like license type, embeddable status, etc.
//     */
//    public boolean isVideoMonetized(String videoId) {
//        try {
//            Video video = youtubeService.getVideoDetails(videoId);
//
//
//            boolean hasStandardLicense = "youtube".equals(
//                    video.getStatus().getLicense()
//            );
//
//            boolean isEmbeddable = Boolean.TRUE.equals(
//                    video.getStatus().getEmbeddable()
//            );
//
//            boolean isPublic = "public".equals(
//                    video.getStatus().getPrivacyStatus()
//            );
//
//            // If video has ads disabled or is marked as made for kids, usually not monetized
//            boolean madeForKids = Boolean.TRUE.equals(
//                    video.getStatus().getMadeForKids()
//            );
//
//            // Monetization is likely if it's public, embeddable, has standard license, and not made for kids
//            return hasStandardLicense && isEmbeddable && isPublic && !madeForKids;
//
//        } catch (Exception e) {
//            log.error("Error checking video monetization: {}", e.getMessage());
//            throw new RuntimeException("Failed to check monetization status");
//        }
//    }
//
//    /**
//     * Check if a channel is monetized
//     */
//    public boolean isChannelMonetized(String channelId) {
//        try {
//            Channel channel = youtubeService.getChannelDetails(channelId);
//
//            Long subscriberCount = channel.getStatistics().getSubscriberCount() != null
//                    ? channel.getStatistics().getSubscriberCount().longValue() : 0L;
//
//            Long videoCount = channel.getStatistics().getVideoCount() != null
//                    ? channel.getStatistics().getVideoCount().longValue() : 0L;
//
//            Long viewCount = channel.getStatistics().getViewCount() != null
//                    ? channel.getStatistics().getViewCount().longValue() : 0L;
//
//
//            return subscriberCount >= 1000 && viewCount >= 100000 && videoCount >= 10;
//
//        } catch (Exception e) {
//            log.error("Error checking channel monetization: {}", e.getMessage());
//            throw new RuntimeException("Failed to check channel monetization status");
//        }
//    }
//}
