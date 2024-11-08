package com.urlshortener.model;

import lombok.Data;

import org.hibernate.validator.constraints.URL;
import org.springframework.format.annotation.DateTimeFormat;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
public class UrlDto {
    @NotEmpty(message = "URL cannot be empty")
    @URL(message = "Please enter a valid URL")
    private String originalUrl;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime expiryDate;
}