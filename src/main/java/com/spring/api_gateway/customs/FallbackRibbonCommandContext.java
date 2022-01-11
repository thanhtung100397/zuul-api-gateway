package com.spring.api_gateway.customs;

import org.springframework.cloud.netflix.ribbon.support.RibbonCommandContext;

public class FallbackRibbonCommandContext extends RibbonCommandContext {
    private int availableServerCount;

    public FallbackRibbonCommandContext(RibbonCommandContext ribbonCommandContext, int availableServerCount) {
        super(ribbonCommandContext.getServiceId(),
                ribbonCommandContext.getMethod(),
                ribbonCommandContext.getUri(),
                ribbonCommandContext.getRetryable(),
                ribbonCommandContext.getHeaders(),
                ribbonCommandContext.getParams(),
                ribbonCommandContext.getRequestEntity(),
                ribbonCommandContext.getRequestCustomizers(),
                ribbonCommandContext.getContentLength(),
                ribbonCommandContext.getLoadBalancerKey());
        this.availableServerCount = availableServerCount;
    }

    public boolean hasAvailableServer() {
        return availableServerCount > 0;
    }

    public int getAvailableServerCount() {
        return availableServerCount;
    }

    public void decreaseAvailableServerBy(int number) {
        availableServerCount -= number;
    }
}
