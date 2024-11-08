package com.urlshortener.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.urlshortener.model.Url;
import com.urlshortener.service.UrlService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UrlApiController {
    
    private final UrlService urlService;
    
    @GetMapping("/recent-urls")
    public List<Url> getRecentUrls() {
        return urlService.getRecentUrls();
    } 
}