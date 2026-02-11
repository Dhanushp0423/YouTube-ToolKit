//package com.youtubetoolkit.dto;
//
//import kotlin.BuilderInference;
//import lombok.Builder;
//import lombok.Data;
//
//@Data
//@Builder
//public class ChannelInfoDTO {
//    private String channelId;
//    private String channelTitle;
//    private String customUrl;
//    private String description;
//    private String publishedAt;
//    private Long subscriberCount;
//    private Long videoCount;
//    private Long viewCount;
//    private String thumbnailUrl;
//    private String bannerUrl;
//    private Boolean isMonetized;
//    private String country;
//}
package com.youtubetoolkit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelInfoDTO {
    private String channelId;
    private String channelTitle;
    private String customUrl;
    private String description;
    private String publishedAt;
    private Long subscriberCount;
    private Long videoCount;
    private Long viewCount;
    private String thumbnailUrl;
    private String bannerUrl;
    private boolean isMonetized;
    private String country;
}