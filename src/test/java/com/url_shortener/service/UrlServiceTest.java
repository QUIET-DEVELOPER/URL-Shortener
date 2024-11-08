package com.url_shortener.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.urlshortener.model.Url;
import com.urlshortener.model.UrlDto;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.service.UrlServiceImpl;

@ExtendWith(MockitoExtension.class)
class UrlServiceTest {
    
    @Mock
    private UrlRepository urlRepository;
    
    @InjectMocks
    private UrlServiceImpl urlService;
    
    @Test
    void createShortUrl_ShouldGenerateUniqueShortUrl() {
        // Arrange
        UrlDto urlDto = new UrlDto();
        urlDto.setOriginalUrl("https://example.com");
        
        // Act
        when(urlRepository.save(any(Url.class))).thenAnswer(i -> i.getArguments()[0]);
        Url result = urlService.createShortUrl(urlDto);
        
        // Assert
        assertNotNull(result.getShortUrl());
        assertEquals(urlDto.getOriginalUrl(), result.getOriginalUrl());
        verify(urlRepository).save(any(Url.class));
    }
    
    @Test
    void getOriginalUrl_WhenUrlExists_ShouldReturnUrl() {
        // Arrange
        String shortUrl = "abc123";
        Url url = new Url();
        url.setOriginalUrl("https://example.com");
        url.setShortUrl(shortUrl);
        url.setExpiryDate(LocalDateTime.now().plusDays(1)); // Set expiry date to future
        
        // Act
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));
        Url result = urlService.getOriginalUrl(shortUrl);
        
        // Assert
        assertEquals(url.getOriginalUrl(), result.getOriginalUrl());
        verify(urlRepository).findByShortUrl(shortUrl);
    }
    
    @Test
    void getOriginalUrl_WhenUrlExpired_ShouldThrowException() {
        // Arrange
        String shortUrl = "abc123";
        Url url = new Url();
        url.setExpiryDate(LocalDateTime.now().minusDays(1)); // Set expiry date to past
        
        // Act & Assert
        when(urlRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(url));
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> urlService.getOriginalUrl(shortUrl));
        
        // Optionally check the message of the exception
        assertEquals("URL has expired", thrown.getMessage());
    }
}
