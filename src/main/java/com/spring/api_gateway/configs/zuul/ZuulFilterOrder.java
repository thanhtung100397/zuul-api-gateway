package com.spring.api_gateway.configs.zuul;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

public class ZuulFilterOrder {

    //pre filter
    public static final int GLOBAL_REQUEST_FILTER_ORDER = 0;
    public static final int GLOBAL_FILTER_ORDER = 3;
    public static final int AUTHENTICATION_FILTER_ORDER = 4;
    public static final int FORWARD_ENDPOINT_RESOLVE_FILTER_ORDER = FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;

    //post filter
    public static final int GLOBAL_RESPONSE_FILTER_ORDER = 0;

    //error filter
    public static final int GLOBAL_EXCEPTION_FILTER_ORDER = FilterConstants.SEND_ERROR_FILTER_ORDER - 1;
}
