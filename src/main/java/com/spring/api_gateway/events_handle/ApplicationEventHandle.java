package com.spring.api_gateway.events_handle;

public interface ApplicationEventHandle {
    void handleEvent() throws Exception;
    String startMessage();
    String successMessage();
}
