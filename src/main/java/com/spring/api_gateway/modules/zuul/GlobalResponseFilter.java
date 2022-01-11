package com.spring.api_gateway.modules.zuul;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.util.Pair;
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
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GlobalResponseFilter extends ZuulFilter {
    private static Logger logger = LoggerFactory.getLogger(GlobalResponseFilter.class);
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    public static final String TEXT_TYPE = "text/";

    @Value("${application.logging.enable:true}")
    private boolean enableLogging;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String filterType() {
        return ZuulFilterType.POST.value();
    }

    @Override
    public int filterOrder() {
        return ZuulFilterOrder.GLOBAL_RESPONSE_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = new HttpServletRequestWrapper(context.getRequest());
        InputStream responseIS = context.getResponseDataStream();

        boolean textBody = false;
        Map<String, String> headers = new HashMap<>();
        List<Pair<String, String>> responseHeaders = context.getOriginResponseHeaders();
        for (Pair<String, String> responseHeader : responseHeaders) {
            String key = responseHeader.first();
            String value = responseHeader.second();
            if (CONTENT_TYPE.equals(key) && (value.startsWith(APPLICATION_JSON) || value.startsWith(TEXT_TYPE))) {
                textBody = true;
            }
            headers.put(key, value);
        }
        String headerString;
        try {
            headerString = objectMapper.writeValueAsString(headers);
        } catch (JsonProcessingException e) {
            headerString = "{}";
        }

        String bodyString;
        if (textBody) {
            try {
                byte[] responseData = StreamUtils.copyToByteArray(responseIS);
                context.setResponseDataStream(new ByteArrayInputStream(responseData));
                if (responseData.length == 0) {
                    bodyString = "{}";
                } else {
                    bodyString = new String(responseData, StandardCharsets.UTF_8);
                }
            } catch (IOException e) {
                bodyString = "{}";
            }
        } else {
            bodyString = "NOT TEXT";
        }

        if (enableLogging) {
            logger.info("{\"response\": {\"id\":\"{}\",\"service_ip\":\"{}\", \"service_port\":\"{}\", \"host\": \"{}\", \"method\": \"{}\", \"route\": \"{}\", \"header\":{},\"body\": {}}}",
                    context.get(StringConstants.ID),
                    request.getServerName(),
                    request.getServerPort(),
                    request.getServerName(),
                    request.getMethod(),
                    request.getServletPath(),
                    headerString,
                    bodyString);
        }

        return null;
    }
}
