package com.youtubetoolkit.controller;

import com.youtubetoolkit.dto.ChannelInfoDTO;
import com.youtubetoolkit.dto.YouTubeRequest;
import com.youtubetoolkit.entity.SearchLog;
import com.youtubetoolkit.repository.SearchLogRepository;
import com.youtubetoolkit.service.ChannelService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/channel")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ChannelController {

    private final ChannelService channelService;
    private final SearchLogRepository searchLogRepository;

    @PostMapping("/info")
    public ResponseEntity<?> getChannelInfo(
            @Valid @RequestBody YouTubeRequest request,
            HttpServletRequest httpRequest) {

        long startTime = System.currentTimeMillis();
        Map<String, Object> response = new HashMap<>();

        try {
            ChannelInfoDTO channelInfo = channelService.getChannelInfo(request.getUrl());
            response.put("success", true);
            response.put("data", channelInfo);

            logSearch("CHANNEL_INFO", request.getUrl(), httpRequest,
                    (int)(System.currentTimeMillis() - startTime), true, null);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error getting channel info: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());

            logSearch("CHANNEL_INFO", request.getUrl(), httpRequest,
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
//import com.youtubetoolkit.dto.ChannelInfoDTO;
//import com.youtubetoolkit.dto.YouTubeRequest;
//import com.youtubetoolkit.entity.SearchLog;
//import com.youtubetoolkit.repository.SearchLogRepository;
//import com.youtubetoolkit.service.ChannelService;
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
//@RequestMapping("/api/channel")
//@RequiredArgsConstructor
//@Slf4j
//@CrossOrigin(origins = "*")
//public class ChannelController {
//
//    private final ChannelService channelService;
//    private final SearchLogRepository searchLogRepository;
//
//    @PostMapping("/info")
//    public ResponseEntity<?> getChannelInfo(@Valid @RequestBody YouTubeRequest request, HttpServletRequest httpRequest) {
//        long startTime = System.currentTimeMillis();
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            ChannelInfoDTO channelInfoDTO = channelService.getChannelInfo(request.getUrl());
//            response.put("success", true);
//            response.put("data", channelInfoDTO);
//
//            logSearch("CHANNEL_INFO", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), true, null);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            log.error("Error getting Channel Info: {}", e.getMessage());
//            response.put("success", false);
//            response.put("error", e.getMessage());
//
//            logSearch("CHANNEL_INFO", request.getUrl(), httpRequest,
//                    (int)(System.currentTimeMillis() - startTime), false, e.getMessage());
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
//                    .userAgent(request.getHeader("User_Agent"))
//                    .responseTimeMs(responseTime)
//                    .success(success)
//                    .errorMessage(errorMessage)
//                    .build();
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