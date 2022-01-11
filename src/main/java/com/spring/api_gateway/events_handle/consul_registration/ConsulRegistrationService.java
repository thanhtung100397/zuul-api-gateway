package com.spring.api_gateway.events_handle.consul_registration;

import com.ecwid.consul.v1.agent.model.NewService;
import com.spring.api_gateway.annotations.event.EventHandler;
import com.spring.api_gateway.events_handle.ApplicationEvent;
import com.spring.api_gateway.events_handle.ApplicationEventHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.consul.discovery.ConsulDiscoveryProperties;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulServiceRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@EventHandler(event = ApplicationEvent.ON_APPLICATION_STARTED_UP)
public class ConsulRegistrationService implements ApplicationEventHandle {
    @Autowired(required = false)
    private ConsulServiceRegistry consulServiceRegistry;
    @Autowired(required = false)
    private ConsulDiscoveryProperties consulDiscoveryProperties;

    @Value("${spring.cloud.consul.enabled:true}")
    private boolean isConsulEnabled;
    @Value("${spring.cloud.service-registry.auto-registration.enabled:true}")
    private boolean isAutoConfigurationEnabled;
    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${server.port}")
    private int serverPort;
    @Value("${http.port:-1}")
    private int httpPort;
    @Value("${spring.cloud.consul.discovery.health-check-protocol:http}")
    private String consulHealthCheckProtocol;
    @Value("${spring.cloud.consul.discovery.health-check-path:/actuator/health}")
    private String consulHealthCheckPath;
    @Value("${spring.cloud.consul.discovery.health-check-interval:10s}")
    private String consulHealthCheckInterval;
    @Value("${spring.cloud.consul.discovery.health-check-timeout:5s}")
    private String consulHealthCheckTimeout;

    @Override
    public String startMessage() {
        return "Start registering service to consul...";
    }

    @Override
    public String successMessage() {
        return "Consul registration...OK";
    }

    @Override
    public void handleEvent() throws Exception {
        if (isConsulEnabled && !isAutoConfigurationEnabled) {
            consulServiceRegistry.register(consulRegistration());
        }
    }

    private ConsulRegistration consulRegistration() {
        int port = httpPort > 0? httpPort : serverPort;
        String healthCheckProtocol = consulHealthCheckProtocol.equalsIgnoreCase("https")? "https" : "http";

        NewService newService = new NewService();
        newService.setId(consulDiscoveryProperties.getHostname() + ":" + port);
        newService.setName(serviceName);
        List<String> serviceTags = new ArrayList<>();
        serviceTags.add("secure=false");
        newService.setTags(serviceTags);
        newService.setAddress(consulDiscoveryProperties.getHostname());
        newService.setPort(serverPort);
        NewService.Check check = new NewService.Check();
        check.setInterval(consulHealthCheckInterval);
        check.setHttp(String.format("%s://%s:%d%s", healthCheckProtocol, consulDiscoveryProperties.getHostname(), port, consulHealthCheckPath));
        check.setTimeout(consulHealthCheckTimeout);
        newService.setCheck(check);
        consulDiscoveryProperties.setInstanceId(newService.getId());
        return new ConsulRegistration(newService, consulDiscoveryProperties);
    }
}
