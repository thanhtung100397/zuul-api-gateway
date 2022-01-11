package com.spring.api_gateway.modules.zuul;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.spring.api_gateway.configs.zuul.ZuulFilterOrder;
import com.spring.api_gateway.constants.StringConstants;
import com.spring.api_gateway.exceptions.gateway.GatewayExpectedException;
import com.spring.api_gateway.modules.gateway.models.dtos.ResponseContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GlobalExceptionFilter extends ZuulFilter {
    private static final String SEND_ERROR_FILTER_RAN = "sendErrorFilter.ran";
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionFilter.class);

    @Override
    public String filterType() {
        return ZuulFilterType.ERROR.value();
    }

    @Override
    public int filterOrder() {
        return ZuulFilterOrder.GLOBAL_EXCEPTION_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        return requestContext.getThrowable() != null &&
                !requestContext.getBoolean(SEND_ERROR_FILTER_RAN, false);
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        Throwable throwable = requestContext.getThrowable();
        try {
            while (true) {
                if (throwable instanceof GatewayExpectedException) {
                    GatewayExpectedException error = (GatewayExpectedException) throwable;
                    responseMessageToClient(error.nStatusCode,
                            error.getResponseCode(),
                            error.getMessage(),
                            error.getResponseContent(),
                            requestContext);
                    break;
                }
                if (throwable.getCause() == null) {
                    throw throwable;
                }
                throwable = throwable.getCause();
            }
        } catch (Throwable e) {
            responseMessageToClient(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                    ResponseContent.MESSAGE_ONLY,
                    requestContext);
            logger.error("{\"unexpected error\": {\"id\":\"" + requestContext.get(StringConstants.ID) + "\"}}", throwable);
        }
        requestContext.set(SEND_ERROR_FILTER_RAN, true);
        return null;
    }

    private void responseMessageToClient(int statusCode, int responseCode, String data,
                                         ResponseContent responseContent,
                                         RequestContext requestContext) {
        HttpServletResponse response = requestContext.getResponse();
        response.setStatus(statusCode);
        requestContext.setSendZuulResponse(false);
        try {
            response.setContentType("application/json; charset=utf-8");
            switch (responseContent) {
                case JSON_BODY: {
                    response.getWriter().write(data);
                }
                break;

                case MESSAGE_ONLY: {
                    response.getWriter().write("{\""+ StringConstants.CODE +"\":" + responseCode + ", \""+ StringConstants.MESSAGE+"\":\"" + data + "\"}");
                }
                break;

                default: {
                    break;
                }
            }
        } catch (IOException ignored) {
        }
    }
}
