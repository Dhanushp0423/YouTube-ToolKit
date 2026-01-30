package com.youtubetoolkit.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VideoInfoDTO {
    private String videoId;
    private String title;
    private String description;
    private String channelId;
    private String channelTitle;
    private String publishedAt;
    private String uploadDate;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private String duration;
    private List<String> tags;
    private String thumbnailUrl;
    private String categoryId;
    private Boolean isMonetized;
    private String definition; // "hd" or "sd"
}

