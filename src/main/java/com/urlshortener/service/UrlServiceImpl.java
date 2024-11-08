package com.urlshortener.service;

import com.urlshortener.model.Url;
import com.urlshortener.model.UrlDto;
import com.urlshortener.repository.UrlRepository;
import com.urlshortener.util.UrlUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {
    
    private final UrlRepository urlRepository;

    @Value("${url.shortener.length}")
    private int shortUrlLength;

    @Override
    @Transactional
    public Url createShortUrl(UrlDto urlDto) {
        Url url = new Url();
        url.setOriginalUrl(urlDto.getOriginalUrl());
        url.setExpiryDate(urlDto.getExpiryDate());
        url.setShortUrl(generateUniqueShortUrl());
        url.setClickCount(0);
        return urlRepository.save(url);
    }

    @Override
    @Transactional(readOnly = true)
    public Url getOriginalUrl(String shortUrl) {
        return urlRepository.findByShortUrl(shortUrl)
            .filter(url -> !isExpired(url))
            .orElseThrow(() -> new RuntimeException("URL not found or expired"));
    }

    @Override
    @Transactional
    public void incrementClickCount(Url url) {
        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);
    }

    @Override
    public List<Url> getRecentUrls() {
        return urlRepository.findTop10ByOrderByCreatedAtDesc();
    }

    private String generateUniqueShortUrl() {
        String shortUrl;
        do {
            shortUrl = UrlUtils.generateShortUrl(shortUrlLength);
        } while (urlRepository.findByShortUrl(shortUrl).isPresent());
        return shortUrl;
    }

    private boolean isExpired(Url url) {
        return url.getExpiryDate() != null && 
               url.getExpiryDate().isBefore(LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Url> getAllUrls() {
        return urlRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUrlCount() {
        return urlRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUrlCount() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.countByExpiryDateIsNullOrExpiryDateAfter(now);
    }

    @Override
    @Transactional(readOnly = true)
    public long getExpiredUrlCount() {
        LocalDateTime now = LocalDateTime.now();
        return urlRepository.countByExpiryDateBefore(now);
    }

    @Scheduled(fixedRate = 86400000) // Run once per day
    @Transactional
    public void cleanupExpiredUrls() {
        LocalDateTime now = LocalDateTime.now();
        List<Url> expiredUrls = urlRepository.findByExpiryDateBefore(now);
        urlRepository.deleteAll(expiredUrls);
    }
}
