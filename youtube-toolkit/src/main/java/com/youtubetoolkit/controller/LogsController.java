package com.youtubetoolkit.controller;

import com.youtubetoolkit.entity.SearchLog;
import com.youtubetoolkit.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LogsController {

    private final SearchLogRepository searchLogRepository;

    @GetMapping("/all")
    public ResponseEntity<List<SearchLog>> getAllLogs() {
        try {
            List<SearchLog> logs = searchLogRepository.findTop100ByOrderByCreatedAtDesc();
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching logs: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/by-type/{searchType}")
    public ResponseEntity<List<SearchLog>> getLogsByType(@PathVariable String searchType) {
        try {
            List<SearchLog> logs = searchLogRepository.findBySearchType(searchType);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("Error fetching logs by type: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            List<SearchLog> allLogs = searchLogRepository.findAll();

            long total = allLogs.size();
            long successful = allLogs.stream().filter(SearchLog::isSuccess).count();
            long failed = total - successful;

            Map<String, Long> typeCount = new HashMap<>();
            typeCount.put("VIDEO_INFO", searchLogRepository.countBySearchType("VIDEO_INFO"));
            typeCount.put("CHANNEL_INFO", searchLogRepository.countBySearchType("CHANNEL_INFO"));
            typeCount.put("THUMBNAIL_DOWNLOAD", searchLogRepository.countBySearchType("THUMBNAIL_DOWNLOAD"));
            typeCount.put("TAG_EXTRACTION", searchLogRepository.countBySearchType("TAG_EXTRACTION"));
            typeCount.put("MONETIZATION_CHECK", searchLogRepository.countBySearchType("MONETIZATION_CHECK"));

            Map<String, Object> stats = new HashMap<>();
            stats.put("total", total);
            stats.put("successful", successful);
            stats.put("failed", failed);
            stats.put("byType", typeCount);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching statistics: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}