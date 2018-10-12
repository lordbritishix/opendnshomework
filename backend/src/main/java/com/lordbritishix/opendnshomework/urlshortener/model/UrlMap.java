package com.lordbritishix.opendnshomework.urlshortener.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UrlMap {
    @Id
    private long id;
    private String originalUrl;
    private String mappedUrl;
}