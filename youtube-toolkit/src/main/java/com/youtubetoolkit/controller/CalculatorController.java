package com.youtubetoolkit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CalculatorController {

    @PostMapping("/estimate")
    public ResponseEntity<?> calculateEarnings(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long views = Long.valueOf(request.get("views").toString());

            double minRpm = 0.25;
            double maxRpm = 4.00;
            double avgRpm = 2.00;

            double minEarnings = (views / 1000.0) * minRpm;
            double maxEarnings = (views / 1000.0) * maxRpm;
            double avgEarnings = (views / 1000.0) * avgRpm;

            response.put("success", true);
            response.put("views", views);
            response.put("estimatedEarnings", Map.of(
                    "minimum", String.format("$%.2f", minEarnings),
                    "maximum", String.format("$%.2f", maxEarnings),
                    "average", String.format("$%.2f", avgEarnings)
            ));

            response.put("disclaimer", "These are estimates. Actual earnings vary based on factors like CPM, niche, and audience location.");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error calculating earnings: {}", e.getMessage());
            response.put("success", false);
            response.put("error", "Invalid input. Please provide view count.");

            return ResponseEntity.badRequest().body(response);
        }
    }
}

//package com.youtubetoolkit.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/calculator")
//@Slf4j
//@CrossOrigin(origins = "*")
//public class CalculatorController {
//
//    @PostMapping("/estimate")
//    public ResponseEntity<?> calculateEarnings(@RequestBody Map<String,Object> request){
//        Map<String,Object> response = new HashMap<>();
//
//        try{
//            Long views = Long.valueOf(request.get("views").toString());
//
//            double minRpm = 0.25;
//            double maxRpm = 4.00;
//            double avgRpm = 2.00;
//
//            double minEarnings = (views/1000.0)*minRpm;
//            double maxEarnings = (views/1000.0)*maxRpm;
//            double avgEarnings = (views/1000.0)*avgRpm;
//
//            response.put("success", true);
//            response.put("views", views);
//            response.put("estimatedEarnings", Map.of(
//                    "minimum", String.format("$%.2f", minEarnings),
//                    "maximum", String.format("$%.2f", maxEarnings),
//                    "average", String.format("$%.2f", avgEarnings)
//            ));
//
//            response.put("disclaimer", "These are estimates. Actual earnings vary based on factors like CPM, niche, local audience");
//            return ResponseEntity.ok(response);
//
//        } catch (Exception e) {
//            log.error("Error calculating earnings: {}", e.getMessage());
//            response.put("success", false);
//            response.put("error", "Invalid input. Please provide view count.");
//
//            return ResponseEntity.badRequest().body(response);
//        }
//    }
//
//}
