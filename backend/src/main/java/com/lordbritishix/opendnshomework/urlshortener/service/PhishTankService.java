package com.lordbritishix.opendnshomework.urlshortener.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import javax.annotation.PostConstruct;
import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import com.google.gson.stream.JsonReader;
import com.lordbritishix.opendnshomework.urlshortener.model.PhishInfo;
import com.lordbritishix.opendnshomework.urlshortener.repository.PhishInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Responsible for analyzing URLS to see if it is a phishing site. It relies on the phish tank's phishing database to figure
 * it out. When this service loads, it populates the database with the local copy of phish tank's database if it is empty.
 * It then periodically updates by pulling the latest phish tank database from http://data.phishtank.com/data/online-valid.json.gz.
 */
@Service
@Slf4j
public class PhishTankService {
    private final String offlineDatabaseName;
    private final String onlineDbUrl;
    private final PhishInfoRepository phishInfoRepository;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
    private final Object LOCK = new Object();

    private static final int BATCH_SIZE = 1000;

    @PostConstruct
    public void init() throws IOException {
        updatePhishingDatabaseFromOffline();

        Retryer<Boolean> retryer = RetryerBuilder.<Boolean>newBuilder()
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(10))
                .withWaitStrategy(WaitStrategies.exponentialWait())
                .build();


        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
                        // build some resiliency when fetching phishing database online by retrying non-509 failures
                        retryer.call(() -> {
                            updatePhishingDatabaseFromOnline();
                            return null;
                        });
                    } catch (RuntimeException e) {
                        log.warn("There was a problem in refreshing the phish tank database - will retry periodically", e);
                    } catch (ExecutionException | RetryException e) {
                        log.warn("Retryer was interrupted", e);
                    }
                }, 0L, 1L, TimeUnit.HOURS);
    }

    @Autowired
    public PhishTankService(
            @Value("${phishtank.key}") String key,
            @Value("${phishtank.offlinedatabasename}") String offlineDatabaseName,
            @Value("${phishtank.onlinedatabasenametemplate}") String onlineDatabaseNameTemplate,
            PhishInfoRepository phishInfoRepository) {
        this.phishInfoRepository = phishInfoRepository;
        this.offlineDatabaseName = offlineDatabaseName;
        this.onlineDbUrl = String.format(onlineDatabaseNameTemplate, key);
    }

    public boolean isPhish(URI url) {
        return phishInfoRepository.existsByUrl(url.toString());
    }

    private void updatePhishingDatabaseFromOffline() throws IOException {
        // Only transfer offline phish db to application db if there is nothing in the application db.
        // This is to make the service immediately available at startup
        if (phishInfoRepository.count() > 0) {
            return;
        }

        log.info("Updating phishing database from {}", offlineDatabaseName);

        try (InputStream inputStream = new BufferedInputStream(PhishTankService.class.getResourceAsStream(offlineDatabaseName));
                GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);
                JsonReader jsonReader = new JsonReader(bufferedReader)) {
            writePhishInfosToDb(jsonReader);
        }
    }

    private void updatePhishingDatabaseFromOnline() throws IOException {
        log.info("Updating phishing database from {}", onlineDbUrl);

        // phishtank endpoint returns redirect, so follow it until we can get to the non-redirect URL
        Optional<String> url = getFinalURL(onlineDbUrl);

        if (!url.isPresent()) {
            // request limit reached, don't do anything
            log.warn("Request limit reached, will retry periodically");
            return;
        }

        try (InputStream inputStream = new URL(url.get()).openStream();
            GZIPInputStream gzipInputStream = new GZIPInputStream(inputStream);
             InputStreamReader reader = new InputStreamReader(gzipInputStream);
             BufferedReader bufferedReader = new BufferedReader(reader);
             JsonReader jsonReader = new JsonReader(bufferedReader)) {
            writePhishInfosToDb(jsonReader);
        }
    }

    private void writePhishInfosToDb(JsonReader reader) throws IOException {
        List<PhishInfo> phishInfos = new ArrayList<>();
        reader.beginArray();

        while (reader.hasNext()) {
            phishInfos.add(readPhishInfo(reader));

            // Batch the saves to the database to improve performance
            if (phishInfos.size() >= BATCH_SIZE) {
                synchronized (LOCK) {
                    log.info("Persisting {} records phishing data to database", phishInfos.size());
                    phishInfoRepository.saveAll(phishInfos);
                }

                phishInfos.clear();
            }
        }

        synchronized (LOCK) {
            log.info("Persisting {} records phishing data to database", phishInfos.size());
            phishInfoRepository.saveAll(phishInfos);
        }

        phishInfos.clear();

        reader.endArray();
    }

    private PhishInfo readPhishInfo(JsonReader reader) throws IOException {
        PhishInfo phishInfo = new PhishInfo();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "phish_id":
                    phishInfo.setPhishId(Long.parseLong(reader.nextString()));
                    break;

                case "url":
                    phishInfo.setUrl(reader.nextString());
                    break;

                case "submission_time":
                    phishInfo.setSubmissionTime(reader.nextString());
                    break;

                case "verification_time":
                    phishInfo.setSubmissionTime(reader.nextString());
                    break;

                case "online":
                    phishInfo.setOnline(Boolean.getBoolean(reader.nextString()));
                    break;

                default:
                    reader.skipValue();
            }
        }
        reader.endObject();

        return phishInfo;
    }

    private static Optional<String> getFinalURL(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();

        if (con.getResponseCode() == 509) {
            return Optional.empty();
        }

        con.setInstanceFollowRedirects(false);
        con.connect();

        try (InputStream inputStream = con.getInputStream()) {
            if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP) {
                String redirectUrl = con.getHeaderField("Location");
                return getFinalURL(redirectUrl);
            }
            return Optional.of(url);
        }
    }
}
