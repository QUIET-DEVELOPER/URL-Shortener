package com.urlshortener.repository;


import com.urlshortener.model.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UrlRepository extends JpaRepository<Url, Long> {
    
    Optional<Url> findByShortUrl(String shortUrl);

    List<Url> findTop10ByOrderByCreatedAtDesc();

    long countByExpiryDateIsNullOrExpiryDateAfter(LocalDateTime now);

    long countByExpiryDateBefore(LocalDateTime now);

    List<Url> findByExpiryDateBefore(LocalDateTime now);
}
