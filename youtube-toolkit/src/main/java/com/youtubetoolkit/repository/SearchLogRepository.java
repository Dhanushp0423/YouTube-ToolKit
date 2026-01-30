package com.youtubetoolkit.repository;

import com.youtubetoolkit.entity.SearchLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchLogRepository extends JpaRepository<SearchLog,Long> {
    List<SearchLog> findBySearchType(String searchType);
    List<SearchLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    Long countBySearchType(String searchType);
}
