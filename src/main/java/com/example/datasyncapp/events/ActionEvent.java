package com.example.datasyncapp.events;

import org.springframework.context.ApplicationEvent;

/**
 * This is the event, which will represent when certain action happen like Data Fetched
 * From RDS, Inserted to NoSQL DB etc.
 * In-order to make it and Event needs to extend ApplicationEvent
 */
public class ActionEvent extends ApplicationEvent {
    private final String message;

    public ActionEvent(Object eventObject, String message) {
        super(eventObject);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
