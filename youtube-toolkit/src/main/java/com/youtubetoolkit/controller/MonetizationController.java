package com.youtubetoolkit.controller;

import com.youtubetoolkit.dto.YouTubeRequest;
import com.youtubetoolkit.entity.SearchLog;
import com.youtubetoolkit.repository.SearchLogRepository;
import com.youtubetoolkit.service.MonetizationService;
import com.youtubetoolkit.service.YouTubeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/monetization")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MonetizationController {

    private final MonetizationService monetizationService;
    private final YouTubeService youtubeService;
    private final SearchLogRepository searchLogRepository;

    @PostMapping("/check")
    public ResponseEntity<?> checkMonetization(
            @Valid @RequestBody YouTubeRequest request,
            HttpServletRequest httpRequest) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isMonetized;
            String identifier;

            if ("channel".equalsIgnoreCase(request.getType())) {
                identifier = youtubeService.extractChannelId(request.getUrl());
                if (identifier == null) {
                    throw new IllegalArgumentException("Invalid channel URL or ID");
                }
                isMonetized = monetizationService.isChannelMonetized(identifier);
                response.put("type", "channel");
                response.put("channelId", identifier);
            } else {
                identifier = youtubeService.extractVideoId(request.getUrl());
                if (identifier == null) {
                    throw new IllegalArgumentException("Invalid video URL or ID");
                }
                isMonetized = monetizationService.isVideoMonetized(identifier);
                response.put("type", "video");
                response.put("videoId", identifier);
            }

            response.put("isMonetized", isMonetized);
            response.put("message", isMonetized
                    ? "This " + request.getType() + " appears to be monetized"
                    : "This " + request.getType() + " does not appear to be monetized");
            response.put("success", true);

            logSearch("MONETIZATION_CHECK", request.getUrl(), httpRequest,
                    (int)(System.currentTimeMillis() - startTime), true, null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error checking monetization: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());

            logSearch("MONETIZATION_CHECK", request.getUrl(), httpRequest,
                    (int)(System.currentTimeMillis() - startTime), false, e.getMessage());

            return ResponseEntity.badRequest().body(response);
        }
    }

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

//package com.youtubetoolkit.controller;
//
//import com.youtubetoolkit.dto.YouTubeRequest;
//import com.youtubetoolkit.entity.SearchLog;
//import com.youtubetoolkit.repository.SearchLogRepository;
//import com.youtubetoolkit.service.MonetizationService;
//import com.youtubetoolkit.service.YouTubeService;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/monetization")  // ✅ ADDED
//@RequiredArgsConstructor
//@Slf4j
//@CrossOrigin(origins = "*")
//public class MonetizationController {
//
//    private final MonetizationService monetizationService;
//    private final YouTubeService youtubeService;
//    private final SearchLogRepository searchLogRepository;
//
//    @PostMapping("/api/monetization/check")
//    public ResponseEntity<?> checkMonetization(
//            @Valid @RequestBody YouTubeRequest request,
//            HttpServletRequest httpRequest) {
//
//        long startTime = System.currentTimeMillis();
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            boolean isMonetized;
//            String identifier;
//
//            if ("channel".equalsIgnoreCase(request.getType())) {
//                identifier = youtubeService.extractChannelId(request.getUrl());
//                isMonetized = monetizationService.isChannelMonetized(identifier);
//                response.put("type", "channel");
//                response.put("channelId", identifier);
//            } else {
//                identifier = youtubeService.extractVideoId(request.getUrl());
//                isMonetized = monetizationService.isVideoMonetized(identifier);
//                response.put("type", "video");
//                response.put("videoId", identifier);
//            }
//
//            response.put("isMonetized", isMonetized);
//            response.put("message", isMonetized
//                    ? "This " + request.getType() + " appears to be monetized"
//                    : "This " + request.getType() + " does not appear to be monetized");
//            response.put("success", true);
//
//            // Log the search
//            logSearch("MONETIZATION_CHECK", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), true, null);
//
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error checking monetization: {}", e.getMessage());
//            response.put("success", false);
//            response.put("error", e.getMessage());
//
//            // Log the error
//            logSearch("MONETIZATION_CHECK", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), false, e.getMessage());
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//    private void logSearch(String searchType, String query, HttpServletRequest request,
//                           int responseTime, boolean success, String errorMessage) {
//        try {
//            SearchLog log = SearchLog.builder()
//                    .searchType(searchType)
//                    .searchQuery(query)
//                    .ipAddress(getClientIp(request))
//                    .userAgent(request.getHeader("User-Agent"))
//                    .responseTimeMs(responseTime)
//                    .success(success)
//                    .errorMessage(errorMessage)
//                    .build();
//
//            searchLogRepository.save(log);
//        } catch (Exception e) {
//            log.error("Failed to log search: {}", e.getMessage());
//        }
//    }
//
//    private String getClientIp(HttpServletRequest request) {
//        String ip = request.getHeader("X-Forwarded-For");
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getHeader("X-Real-IP");
//        }
//        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
//            ip = request.getRemoteAddr();
//        }
//        return ip;
//    }
//}