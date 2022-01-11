package com.spring.api_gateway.modules.gateway.services;

import com.spring.api_gateway.base.models.BaseResponse;
import com.spring.api_gateway.base.models.BaseResponseBody;
import com.spring.api_gateway.constants.ResponseValue;
import com.spring.api_gateway.modules.gateway.models.dtos.NewRouteDto;
import com.spring.api_gateway.modules.gateway.models.entities.Routing;
import com.spring.api_gateway.modules.gateway.repositories.BusinessRouteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.web.ZuulHandlerMapping;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.function.Predicate;

@Service
public class ApiGatewayService {
    @Autowired
    private ZuulProperties zuulProperties;
    @Autowired
    private ZuulHandlerMapping zuulHandlerMapping;
    @Autowired
    private RestTemplate httpRequest;

    @Autowired
    private BusinessRouteRepository businessRouteRepository;
    @Autowired
    private ApiGatewayService apiGatewayService;

    @Value("${server.port}")
    private int serverPort;

    public BaseResponse getAllRoutes() {
        return new BaseResponse<>(ResponseValue.SUCCESS, zuulProperties.getRoutes());
    }

    public BaseResponse addNewRoutes(Set<NewRouteDto> newRouteDtos,
                                     boolean refresh) {
        Map<String, ZuulProperties.ZuulRoute> addedRoutes = new HashMap<>();
        for (NewRouteDto newRouteDto : newRouteDtos) {
            ZuulProperties.ZuulRoute newRoute = new ZuulProperties.ZuulRoute();
            newRoute.setId(newRouteDto.getId());
            newRoute.setPath(newRouteDto.getPath());
            newRoute.setSensitiveHeaders(newRouteDto.getSensitiveHeaders());
            newRoute.setLocation(newRouteDto.getLocation());
            newRoute.setStripPrefix(newRouteDto.getStripPrefix());
            addedRoutes.put(newRoute.getId(), newRoute);
        }
        zuulProperties.getRoutes().putAll(addedRoutes);
        zuulHandlerMapping.setDirty(true);
        if (refresh) {
            refreshRoute();
        }
        return new BaseResponse<>(ResponseValue.SUCCESS, addedRoutes);
    }

    public void addNewRoutes(List<ZuulProperties.ZuulRoute> newRoutes) {
        for (ZuulProperties.ZuulRoute newRoute : newRoutes) {
            zuulProperties.getRoutes().put(newRoute.getId(), newRoute);
        }
        zuulHandlerMapping.setDirty(true);
    }

    public BaseResponse deleteRoutes(Set<String> routeIDs, boolean refresh) {
        Map<String, ZuulProperties.ZuulRoute> zuulRoutes = zuulProperties.getRoutes();
        Map<String, ZuulProperties.ZuulRoute> removedRoutes = new HashMap<>();

        for (String routeID : routeIDs) {
            ZuulProperties.ZuulRoute route = zuulRoutes.get(routeID);
            if (route != null) {
                zuulRoutes.remove(routeID);
                removedRoutes.put(routeID, route);
            }
        }

        zuulHandlerMapping.setDirty(true);
        if (refresh) {
            refreshRoute();
        }
        return new BaseResponse<>(ResponseValue.SUCCESS, removedRoutes);
    }

    public BaseResponse updateRoutes(boolean refresh) {
        Map<String, ZuulProperties.ZuulRoute> zuulRouteMap = zuulProperties.getRoutes();
        List<Routing> routes = businessRouteRepository.findAll();
        Set<String> routeIDs = new HashSet<>();
        for (Routing route : routes) {
            String id = String.valueOf(route.getId());
            ZuulProperties.ZuulRoute newRoute = new ZuulProperties.ZuulRoute();
            newRoute.setId(id);
            newRoute.setPath(route.getPath());
            newRoute.setStripPrefix(route.getStripPrefix());
            Set<String> sensitiveHeaders = new HashSet<>();
            newRoute.setSensitiveHeaders(sensitiveHeaders);
            newRoute.setLocation(route.getLocation());
            zuulRouteMap.put(id, newRoute);
            routeIDs.add(id);
        }
        zuulRouteMap.entrySet().removeIf(entry -> !routeIDs.contains(entry.getKey()));
        zuulHandlerMapping.setDirty(true);
//        if (refresh) {
//            refreshRoute();
//        }
        return new BaseResponse(ResponseValue.SUCCESS);
    }

    private void refreshRoute() {
        httpRequest.postForEntity("http://127.0.0.1:" + serverPort + "/api/gateway/refresh",
                null,
                BaseResponseBody.class);
    }
}
