package com.spring.api_gateway.events_handle.apply_persistence_routes;

import com.spring.api_gateway.annotations.event.EventHandler;
import com.spring.api_gateway.events_handle.ApplicationEvent;
import com.spring.api_gateway.events_handle.ApplicationEventHandle;
import com.spring.api_gateway.modules.gateway.services.ApiGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@EventHandler(event = ApplicationEvent.ON_APPLICATION_STARTED_UP)
public class ApplyingPersistenceRoutes implements ApplicationEventHandle {
    @Autowired
    private ApiGatewayService apiGatewayService;

    @Override
    public String startMessage() {
        return "Start applying persistence routes...";
    }

    @Override
    public String successMessage() {
        return "Applying persistence routes...OK";
    }

    @Override
    public void handleEvent() throws Exception {
        apiGatewayService.updateRoutes(false);
    }
}
