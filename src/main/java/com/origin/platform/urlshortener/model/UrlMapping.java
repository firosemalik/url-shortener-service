package com.origin.platform.urlshortener.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "url_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UrlMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false, length = 2048, unique = true)
    private String originalUrl;

    @Column(name = "short_code", nullable = false, unique = true)
    private String shortCode;

    private OffsetDateTime createdAt;

    private Integer hitCount;

    @OneToMany(mappedBy = "urlMapping", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AccessLog> accessLogs;

    public List<AccessLog> getAccessLogs() {
        if (this.accessLogs == null) {
            this.accessLogs = new ArrayList<>();
        }
        return this.accessLogs;
    }

    public void updateHit() {
        this.hitCount = this.hitCount + 1;
    }

}
