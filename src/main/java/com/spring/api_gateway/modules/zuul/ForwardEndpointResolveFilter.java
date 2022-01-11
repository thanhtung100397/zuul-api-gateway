package com.spring.api_gateway.modules.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.spring.api_gateway.configs.zuul.ZuulFilterOrder;
import com.spring.api_gateway.constants.StringConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ForwardEndpointResolveFilter extends ZuulFilter {

    @Bean
    public Pattern serviceIDPattern() {
        return Pattern.compile("(/.*)");
    }

    @Autowired
    private Pattern serviceIDPattern;

    @Override
    public String filterType() {
        return ZuulFilterType.PRE.value();
    }

    @Override
    public int filterOrder() {
        return ZuulFilterOrder.FORWARD_ENDPOINT_RESOLVE_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        String serviceID = (String) requestContext.get(StringConstants.SERVICE_ID);
        if (serviceID != null) {
            String requestURI = (String) requestContext.get(StringConstants.REQUEST_URI);
            Matcher matcher = serviceIDPattern.matcher(serviceID);
            if (matcher.find()) {
                String servicePath = matcher.group();
                serviceID = serviceID.substring(0, matcher.start());
                requestURI = servicePath.replace("/**", requestURI);
                requestContext.set(StringConstants.SERVICE_ID, serviceID);
                requestContext.set(StringConstants.REQUEST_URI, requestURI);
            }
        }
        return null;
    }
}
