package com.origin.platform.urlshortener.repository;

import com.origin.platform.urlshortener.model.UrlMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlMapping, Long> {

    Optional<UrlMapping> findByShortCode(String shortCode);

    @Query("SELECT u FROM UrlMapping u LEFT JOIN FETCH u.accessLogs WHERE u.shortCode = :code")
    Page<UrlMapping> findByShortCodeWithLogs(@Param("code") String code, Pageable pageable);
}
