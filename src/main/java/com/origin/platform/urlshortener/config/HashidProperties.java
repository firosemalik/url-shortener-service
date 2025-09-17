package com.origin.platform.urlshortener.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "urlshortener.hashid")
public class HashidProperties {
    private String salt;
    private int minLength;
}
