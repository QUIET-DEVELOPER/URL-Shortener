package com.urlshortener.controller;

import com.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    
    private final UrlService urlService;
    
    @GetMapping
    public String showAdminDashboard(Model model) {
        model.addAttribute("allUrls", urlService.getAllUrls());
        model.addAttribute("totalUrls", urlService.getTotalUrlCount());
        model.addAttribute("activeUrls", urlService.getActiveUrlCount());
        model.addAttribute("expiredUrls", urlService.getExpiredUrlCount());
        return "admin";
    }
}