package com.example.datasyncapp.events;

import com.example.datasyncapp.services.ProviderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listens to published events
 * Need to implement ApplicationListener<T>, T is the type of published event.
 */

@Slf4j
@Component
public class DataSyncedActionEventsListener implements ApplicationListener<ActionEvent> {

    @Override
    public void onApplicationEvent(ActionEvent event) {
        log.info("Event received with message: {} ", event.getMessage());
        // Notify if needed (Further operations)
    }
}
