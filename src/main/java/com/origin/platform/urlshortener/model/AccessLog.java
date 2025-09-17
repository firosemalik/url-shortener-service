package com.origin.platform.urlshortener.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "access_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime accessedAt;

    private String ipAddress;

    private String userAgent;

    private String referrer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_mapping_id", nullable = false)
    private UrlMapping urlMapping;
}
