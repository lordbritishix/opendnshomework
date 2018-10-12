package com.lordbritishix.opendnshomework.urlshortener.service;

import java.io.IOException;
import com.lordbritishix.opendnshomework.urlshortener.repository.PhishInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PhishTankServiceTest {
    private PhishTankService phishTankService;

    @Mock
    private PhishInfoRepository phishInfoRepository;

    @Before
    public void setup() {
        phishTankService = new PhishTankService(
                "abc", "/offline_definition.json.gz", "abc", phishInfoRepository);
    }

    @Test
    public void initDownloadsOfflineDatabaseIfNoRecords() throws IOException {
        when(phishInfoRepository.count()).thenReturn(0L);

        phishTankService.init();
        verify(phishInfoRepository, atLeastOnce()).saveAll(anyObject());
    }

    @Test
    public void initDoesNotDownloadOfflineDatabaseIfHasRecords() throws IOException {
        when(phishInfoRepository.count()).thenReturn(1L);

        phishTankService.init();
        verify(phishInfoRepository, never()).saveAll(anyObject());
    }
}
