package com.origin.platform.urlshortener.repository;

import com.origin.platform.urlshortener.model.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByOriginalUrl(String originalUrl);

    Optional<UrlMapping> findByShortCode(String shortCode);

    @Query("SELECT u FROM UrlMapping u LEFT JOIN FETCH u.accessLogs WHERE u.shortCode = :code")
    Optional<UrlMapping> findByShortCodeWithLogs(@Param("code") String code);
}
