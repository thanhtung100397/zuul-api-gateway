package com.spring.api_gateway.modules.zuul;

import com.netflix.client.ClientException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.spring.api_gateway.constants.ResponseValue;
import com.spring.api_gateway.customs.FallbackRibbonCommandContext;
import com.spring.api_gateway.exceptions.gateway.GatewayExpectedException;
import com.spring.api_gateway.modules.gateway.models.dtos.ResponseContent;
import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter;
import org.springframework.cloud.netflix.zuul.filters.route.apache.HttpClientRibbonCommand;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Map;

@Component
public class FallbackRibbonRoutingFilter extends RibbonRoutingFilter {
    public FallbackRibbonRoutingFilter(ProxyRequestHelper helper,
                                       RibbonCommandFactory<?> ribbonCommandFactory) {
        super(helper, ribbonCommandFactory, Collections.emptyList());
    }

    @Override
    protected ClientHttpResponse forward(RibbonCommandContext context) throws Exception {
        Map<String, Object> info = this.helper.debug(context.getMethod(),
                context.getUri(), context.getHeaders(), context.getParams(),
                context.getRequestEntity());
        RibbonCommand command = this.ribbonCommandFactory.create(context);
        HttpClientRibbonCommand clientRibbonCommand = (HttpClientRibbonCommand) command;
        try {
            ClientHttpResponse response = command.execute();
            this.helper.appendDebug(info, response.getRawStatusCode(), response.getHeaders());
            return response;
        } catch (HystrixRuntimeException e) {
            Throwable cause = e.getCause();
            if (cause instanceof ClientException) {
                cause = cause.getCause();
                if (cause instanceof RuntimeException) {
                    cause = cause.getCause();
                    if (cause instanceof SocketTimeoutException) {
                        if (!(context instanceof FallbackRibbonCommandContext)) {
                            int availableServerCount = clientRibbonCommand.getClient()
                                    .getLoadBalancer()
                                    .getReachableServers().size();
                            context = new FallbackRibbonCommandContext(context, availableServerCount);
                        }
                        return handleTimeout((FallbackRibbonCommandContext) context);
                    }
                }
            }
            return super.handleException(info, e);
        }
    }

    private ClientHttpResponse handleTimeout(FallbackRibbonCommandContext context) throws Exception {
        if (context.hasAvailableServer()) {
            context.decreaseAvailableServerBy(1);
            return forward(context);
        }
        ResponseValue responseValue = ResponseValue.BUSINESS_SERVICE_CONNECTION_TIMEOUT;
        throw new GatewayExpectedException(ResponseContent.MESSAGE_ONLY,
                responseValue.httpStatus().value(),
                responseValue.specialCode(),
                responseValue.message());
    }
}
