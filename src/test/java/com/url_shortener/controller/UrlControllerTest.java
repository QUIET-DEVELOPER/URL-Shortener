package com.url_shortener.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.urlshortener.controller.UrlController;
import com.urlshortener.model.Url;
import com.urlshortener.model.UrlDto;
import com.urlshortener.service.UrlService;

@WebMvcTest(UrlController.class)
@ExtendWith(MockitoExtension.class) // Not necessary but included for consistency
class UrlControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UrlService urlService;
    
    @Test
    void shortenUrl_WithValidInput_ShouldRedirectWithSuccess() throws Exception {
        // Arrange
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("https://example.com");
        Url url = new Url();
        url.setShortUrl("abc123");
        
        // Act
        when(urlService.createShortUrl(any(UrlDto.class))).thenReturn(url);
        
        // Assert
        mockMvc.perform(post("/shorten")
                .param("originalUrl", "https://example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("shortUrl", "abc123"));
    }
    
    @Test
    void redirectToOriginalUrl_WithValidShortUrl_ShouldRedirect() throws Exception {
        // Arrange
        String shortUrl = "abc123";
        Url url = new Url();
        url.setOriginalUrl("https://example.com");
        
        // Act
        when(urlService.getOriginalUrl(shortUrl)).thenReturn(url);
        
        // Assert
        mockMvc.perform(get("/{shortUrl}", shortUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com"));
    }
}
