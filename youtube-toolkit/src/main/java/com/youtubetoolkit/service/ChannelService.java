package com.youtubetoolkit.service;

import com.google.api.services.youtube.model.Channel;
import com.youtubetoolkit.dto.ChannelInfoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelService {

    private final YouTubeService youtubeService;
    private final MonetizationService monetizationService;

    public ChannelInfoDTO getChannelInfo(String url) {
        try {
            String channelId = youtubeService.extractChannelId(url);
            Channel channel = youtubeService.getChannelDetails(channelId);

            boolean isMonetized = monetizationService.isChannelMonetized(channelId);

            return ChannelInfoDTO.builder()
                    .channelId(channelId)
                    .channelTitle(channel.getSnippet().getTitle())
                    .customUrl(channel.getSnippet().getCustomUrl())
                    .description(channel.getSnippet().getDescription())
                    .publishedAt(channel.getSnippet().getPublishedAt().toString())
                    .subscriberCount(channel.getStatistics().getSubscriberCount() != null
                            ? channel.getStatistics().getSubscriberCount().longValue() : 0L)
                    .videoCount(channel.getStatistics().getVideoCount() != null
                            ? channel.getStatistics().getVideoCount().longValue() : 0L)
                    .viewCount(channel.getStatistics().getViewCount() != null
                            ? channel.getStatistics().getViewCount().longValue() : 0L)
                    .thumbnailUrl(channel.getSnippet().getThumbnails().getHigh().getUrl())
                    .bannerUrl(channel.getBrandingSettings().getImage() != null
                            ? channel.getBrandingSettings().getImage().getBannerExternalUrl() : null)
                    .isMonetized(isMonetized)
                    .country(channel.getSnippet().getCountry())
                    .build();

        } catch (Exception e) {
            log.error("Error getting channel info: {}", e.getMessage());
            throw new RuntimeException("Failed to get channel information: " + e.getMessage());
        }
    }
}