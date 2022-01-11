package com.spring.api_gateway.customs;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.zuul.context.RequestContext;
import com.spring.api_gateway.constants.StringConstants;
import com.spring.api_gateway.exceptions.zuul.AllRequestTimeoutException;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.netflix.ribbon.*;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

public class SaveStateRibbonLoadBalancerClient extends RibbonLoadBalancerClient {
    private SpringClientFactory clientFactory;

    public SaveStateRibbonLoadBalancerClient(SpringClientFactory clientFactory) {
        super(clientFactory);
        this.clientFactory = clientFactory;
    }

    private boolean isTimeoutServer(Server server) {
        Set<String> timeoutServiceInstances = (Set<String>) RequestContext.getCurrentContext().get(StringConstants.TIMEOUT_SERVER);
        return timeoutServiceInstances != null && timeoutServiceInstances.contains(server.getHostPort());
    }

    private void addTimeoutServer(Server server) {
        if (server == null) {
            return;
        }
        RequestContext requestContext = RequestContext.getCurrentContext();
        Set<String> timeoutServiceInstances = (Set<String>) requestContext.get(StringConstants.TIMEOUT_SERVER);
        if (timeoutServiceInstances == null) {
            timeoutServiceInstances = new HashSet<>();
        }
        timeoutServiceInstances.add(server.getHostPort());
        requestContext.set(StringConstants.TIMEOUT_SERVER, timeoutServiceInstances);
    }

    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
        Server server = null;
        try {
            ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
            server = getServer(loadBalancer);
            if (server == null) {
                throw new IllegalStateException("No instances available for " + serviceId);
            }
            if (isTimeoutServer(server)) {
                throw new AllRequestTimeoutException("All service instances of '" + serviceId + "' timeout");
            }
            RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server,
                    serviceId), serverIntrospector(serviceId).getMetadata(server));
            return execute(serviceId, ribbonServer, request);
        } catch (IOException e) {
            if (e instanceof SocketTimeoutException) {
                //fallback request to other service instance by recursive call this function
                addTimeoutServer(server);
                return execute(serviceId, request);
            } else {
                throw e;
            }
        }
    }

    @Override
    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return super.getLoadBalancer(serviceId);
    }

    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        ServerIntrospector serverIntrospector = serverIntrospector(serviceId);
        return RibbonUtils.isSecure(config, serverIntrospector, server);
    }

    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }
}
