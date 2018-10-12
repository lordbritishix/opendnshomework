package com.lordbritishix.opendnshomework.urlshortener.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import lombok.Data;

@Entity
@Data
public class PhishInfo {
    @Id
    private long phishId;

    @Column
    @Lob
    private String url;
    private String submissionTime;
    private String verifiedTime;
    private boolean online;
}
