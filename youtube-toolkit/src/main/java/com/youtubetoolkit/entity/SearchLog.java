package com.youtubetoolkit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "search_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String searchType;

    @Column(length = 1000)
    private String searchQuery;

    private String ipAddress;

    @Column(length = 500)
    private String userAgent;

    private Integer responseTimeMs;

    private boolean success;

    @Column(length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

//package com.youtubetoolkit.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name="search_logs")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class SearchLog {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "search_type",nullable = false,length = 50)
//    private String searchType;
//
//    @Column(name = "search_query",nullable = false,length = 500)
//    private String searchQuery;
//
//    @Column(name = "ip_address",length = 50)
//    private String ipAddress;
//
//    @Column(name = "user_agent",columnDefinition = "TEXT")
//    private String userAgent;
//
//    @Column(name ="response_time_ms")
//    private Integer responseTimeMs;
//
//    @Column(name ="Success")
//    private Boolean success = true;
//
//    @Column(name = "error_message",columnDefinition = "TEXT")
//    private String errorMessage;
//
//    @Column(name = "created_at",nullable = false,updatable = false)
//    private LocalDateTime createdAt;
//
//    @PrePersist
//    protected void onCreate(){
//        createdAt = LocalDateTime.now();
//    }
//}
