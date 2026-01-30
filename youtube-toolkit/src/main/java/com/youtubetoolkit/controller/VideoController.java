package com.youtubetoolkit.controller;

import com.google.api.services.youtube.model.Video;
import com.youtubetoolkit.dto.VideoInfoDTO;
import com.youtubetoolkit.dto.YouTubeRequest;
import com.youtubetoolkit.entity.SearchLog;
import com.youtubetoolkit.repository.SearchLogRepository;
import com.youtubetoolkit.service.YouTubeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class VideoController {

    private  final YouTubeService youTubeService;
    private final SearchLogRepository searchLogRepository;

    @PostMapping("/info")
    public ResponseEntity<?> getVideoInfo(@Valid @RequestBody YouTubeRequest request, HttpServletRequest httpRequest){

        long startTime = System.currentTimeMillis();
        Map<String,Object> response = new HashMap<>();

        try {
            String videoId = youTubeService.extractVideoId(request.getUrl());
            Video video = youTubeService.getVideoDetails(videoId);

            VideoInfoDTO videoInfo = VideoInfoDTO.builder()
                    .videoId(videoId)
                    .title(video.getSnippet().getTitle())
                    .description(video.getSnippet().getDescription())
                    .channelId(video.getSnippet().getChannelId())
                    .channelTitle(video.getSnippet().getChannelTitle())
                    .publishedAt(video.getSnippet().getPublishedAt().toString())
                    .viewCount(video.getStatistics().getViewCount() != null
                            ? video.getStatistics().getViewCount().longValue() : 0L)
                    .likeCount(video.getStatistics().getLikeCount() != null
                            ? video.getStatistics().getLikeCount().longValue() : 0L)
                    .commentCount(video.getStatistics().getCommentCount() != null
                            ? video.getStatistics().getCommentCount().longValue() : 0L)
                    .duration(video.getContentDetails().getDuration())
                    .tags(video.getSnippet().getTags())
                    .thumbnailUrl(video.getSnippet().getThumbnails().getHigh().getUrl())
                    .categoryId(video.getSnippet().getCategoryId())
                    .definition(video.getContentDetails().getDefinition())
                    .build();
            response.put("success",true);
            response.put("data",videoInfo);

            logSearch("VIDEO_INFO", request.getUrl(),httpRequest,(int)(System.currentTimeMillis()-startTime),true,null);

            return ResponseEntity.ok(request);
        }
        catch (Exception e){
            log.error("Error getting video info:{}",e.getMessage());
            response.put("success",false);
            response.put("error",e.getMessage());


            logSearch("VIDEO_INFO",request.getUrl(),httpRequest,(int)(System.currentTimeMillis()-startTime),false,e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


//    @PostMapping("/tags")
//    public ResponseEntity<?> getVideoTags(
//            @Valid @RequestBody YouTubeRequest request,
//            HttpServletRequest httpRequest) {
//
//        long startTime = System.currentTimeMillis();
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String videoId = youTubeService.extractVideoId(request.getUrl());
//            Video video = youTubeService.getVideoDetails(videoId);
//
//            List<String> tags = video.getSnippet().getTags();
//            response.put("success", true);
//            response.put("videoId", videoId);
//            response.put("title", video.getSnippet().getTitle());
//            response.put("tags", tags != null ? tags : List.of());
//            response.put("tagCount", tags != null ? tags.size() : 0);
//
//            logSearch("TAG_EXTRACTION", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), true, null);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error extracting tags: {}", e.getMessage());
//            response.put("success", false);
//            response.put("error", e.getMessage());
//
//            logSearch("TAG_EXTRACTION", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), false, e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

//    @PostMapping("/thumbnail")
//    public ResponseEntity<?> getThumbnail(
//            @Valid @RequestBody YouTubeRequest request,
//            HttpServletRequest httpRequest) {
//
//        long startTime = System.currentTimeMillis();
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            String videoId = youTubeService.extractVideoId(request.getUrl());
//            Video video = youTubeService.getVideoDetails(videoId);
//
//            Map<String, String> thumbnails = new HashMap<>();
//            var thumbs = video.getSnippet().getThumbnails();
//
//            if (thumbs.getDefault() != null) {
//                thumbnails.put("default", thumbs.getDefault().getUrl());
//            }
//            if (thumbs.getMedium() != null) {
//                thumbnails.put("medium", thumbs.getMedium().getUrl());
//            }
//            if (thumbs.getHigh() != null) {
//                thumbnails.put("high", thumbs.getHigh().getUrl());
//            }
//            if (thumbs.getStandard() != null) {
//                thumbnails.put("standard", thumbs.getStandard().getUrl());
//            }
//            if (thumbs.getMaxres() != null) {
//                thumbnails.put("maxres", thumbs.getMaxres().getUrl());
//            }
//
//            response.put("success", true);
//            response.put("videoId", videoId);
//            response.put("title", video.getSnippet().getTitle());
//            response.put("thumbnails", thumbnails);
//
//            logSearch("THUMBNAIL_DOWNLOAD", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), true, null);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error getting thumbnail: {}", e.getMessage());
//            response.put("success", false);
//            response.put("error", e.getMessage());
//
//            logSearch("THUMBNAIL_DOWNLOAD", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), false, e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    private void logSearch(String searchType, String query, HttpServletRequest request,
                           int responseTime, boolean success, String errorMessage) {
        try {
            SearchLog log = SearchLog.builder()
                    .searchType(searchType)
                    .searchQuery(query)
                    .ipAddress(getClientIp(request))
                    .userAgent(request.getHeader("User-Agent"))
                    .responseTimeMs(responseTime)
                    .success(success)
                    .errorMessage(errorMessage)
                    .build();

            searchLogRepository.save(log);
        } catch (Exception e) {
            log.error("Failed to log search: {}", e.getMessage());
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}


