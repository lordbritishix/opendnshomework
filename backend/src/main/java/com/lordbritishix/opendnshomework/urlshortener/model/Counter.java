package com.lordbritishix.opendnshomework.urlshortener.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
/**
 * Used as a helper to generate seed value for the string shortener.
 */
public class Counter {
    @Id
    @SequenceGenerator(initialValue = 10000,
            allocationSize = 1,
            name = "sequencer",
            sequenceName = "sequencer")
    @GeneratedValue(generator = "sequencer")
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
