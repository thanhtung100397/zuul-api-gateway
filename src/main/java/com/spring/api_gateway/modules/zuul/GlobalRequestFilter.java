package com.spring.api_gateway.modules.zuul;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.spring.api_gateway.configs.zuul.ZuulFilterOrder;
import com.spring.api_gateway.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class GlobalRequestFilter extends ZuulFilter {
    private static Logger logger = LoggerFactory.getLogger(GlobalRequestFilter.class);

    @Value("${application.logging.enable:true}")
    private boolean enableLogging;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String filterType() {
        return ZuulFilterType.PRE.value();
    }

    @Override
    public int filterOrder() {
        return ZuulFilterOrder.GLOBAL_REQUEST_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        context.set(StringConstants.ID, UUID.randomUUID());

        HttpServletRequest request = new HttpServletRequestWrapper(context.getRequest());

        InputStream bodyInputStream = (InputStream) context.get("requestEntity");
        String bodyString = "";
        if (bodyInputStream == null) {
            try {
                bodyInputStream = context.getRequest().getInputStream();
                bodyString = StreamUtils.copyToString(bodyInputStream, Charset.forName("UTF-8"));
                JsonNode jsonNode = objectMapper.readValue(bodyString, JsonNode.class);
                bodyString = jsonNode.toString();
            } catch (IOException e) {
                bodyString = "{}";
            }
        }

        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        String headerString;
        try {
            headerString = objectMapper.writeValueAsString(headers);
        } catch (JsonProcessingException e) {
            headerString = "{}";
        }

        if (enableLogging) {
            logger.info("{\"request\": {\"id\":\"{}\",\"client_ip\":\"{}\",\"host\": \"{}\", \"port\": \"{}\", \"method\": \"{}\", \"route\": \"{}\", \"header\":{}, \"body\": {}}}",
                    context.get(StringConstants.ID),
                    request.getRemoteAddr(),
                    request.getServerName(),
                    request.getServerPort(),
                    request.getMethod(),
                    request.getServletPath(),
                    headerString,
                    bodyString);
        }

        return null;
    }
}
