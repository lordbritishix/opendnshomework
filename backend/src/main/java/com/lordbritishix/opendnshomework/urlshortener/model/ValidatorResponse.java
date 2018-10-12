package com.lordbritishix.opendnshomework.urlshortener.model;

import lombok.Data;

@Data
public class ValidatorResponse {
    private final String message;
    private final boolean isPhishingSite;
}
