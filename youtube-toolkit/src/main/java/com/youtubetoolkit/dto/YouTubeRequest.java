package com.youtubetoolkit.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class YouTubeRequest {

    @NotBlank(message = "URL is required")
    private String url;
    private String type;
}
