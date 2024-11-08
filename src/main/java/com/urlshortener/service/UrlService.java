package com.urlshortener.service;

import com.urlshortener.model.Url;
import com.urlshortener.model.UrlDto;
import java.util.List;

public interface UrlService {
    Url createShortUrl(UrlDto urlDto);
    Url getOriginalUrl(String shortUrl);
    List<Url> getRecentUrls();
    void incrementClickCount(Url url);
    List<Url> getAllUrls();
    long getTotalUrlCount();
    long getActiveUrlCount();
    long getExpiredUrlCount();

}