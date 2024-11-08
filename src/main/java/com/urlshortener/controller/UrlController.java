package com.urlshortener.controller;

import com.urlshortener.model.Url;
import com.urlshortener.model.UrlDto;
import com.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UrlController {

    private final UrlService urlService;
    
    @GetMapping("/")
    public String showHomePage(Model model) {
        model.addAttribute("urlDto", new UrlDto());
        model.addAttribute("recentUrls", urlService.getRecentUrls());
        return "index";
    }
    
    @PostMapping("/shorten")
    public String shortenUrl(
        @Valid @ModelAttribute UrlDto urlDto,
        BindingResult result,
        RedirectAttributes redirectAttributes
    ) {
        if (result.hasErrors()) {
            return "index";
        }
        
        try {
            Url shortenedUrl = urlService.createShortUrl(urlDto);
            redirectAttributes.addFlashAttribute("success", 
                "URL shortened successfully!");
            redirectAttributes.addFlashAttribute("shortUrl", 
                shortenedUrl.getShortUrl());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error creating short URL");
        }
        
        return "redirect:/";
    }
    
    @GetMapping("/{shortUrl}")
    public String redirectToOriginalUrl(
        @PathVariable String shortUrl,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Url url = urlService.getOriginalUrl(shortUrl);
            urlService.incrementClickCount(url);
            return "redirect:" + url.getOriginalUrl();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", 
                "URL not found or has expired");
            return "redirect:/";
        }
    }
}