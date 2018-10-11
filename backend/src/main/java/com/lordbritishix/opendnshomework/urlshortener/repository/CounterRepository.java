package com.lordbritishix.opendnshomework.urlshortener.repository;

import com.lordbritishix.opendnshomework.urlshortener.model.Counter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounterRepository extends JpaRepository<Counter, Long> {
}
