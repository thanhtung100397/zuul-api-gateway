package com.spring.api_gateway.modules.gateway.controllers;

import com.spring.api_gateway.annotations.swagger.Response;
import com.spring.api_gateway.annotations.swagger.Responses;
import com.spring.api_gateway.auth.AuthorizationRequired;
import com.spring.api_gateway.base.controllers.BaseRESTController;
import com.spring.api_gateway.base.models.BaseResponse;
import com.spring.api_gateway.base.models.BaseResponseBody;
import com.spring.api_gateway.modules.gateway.services.ApiGatewayService;
import com.spring.api_gateway.constants.ResponseValue;
import com.spring.api_gateway.modules.gateway.models.dtos.NewRouteDto;
import com.spring.api_gateway.swagger.gateway.ZuulRoutesSwagger;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/gateway")
public class GatewayController extends BaseRESTController {
    @Autowired
    private ApiGatewayService apiGatewayService;


    @ApiOperation(value = "Xem danh sách routing", response = Iterable.class)
    @Responses(value = {
            @Response(responseValue = ResponseValue.SUCCESS, responseBody = ZuulRoutesSwagger.class)
    })
    @AuthorizationRequired
    @GetMapping("/routes")
    public BaseResponse getRoutes() {
        return apiGatewayService.getAllRoutes();
    }

    @ApiOperation(value = "Thêm hoặc cập nhật routing", response = Iterable.class)
    @Responses(value = {
            @Response(responseValue = ResponseValue.SUCCESS, responseBody = ZuulRoutesSwagger.class)
    })
    @AuthorizationRequired
    @PostMapping("/routes")
    public BaseResponse addNewRoutes(@RequestParam(value = "refresh", defaultValue = "true") boolean refresh,
                                     @RequestBody Set<NewRouteDto> newRouteDtos) {
        return apiGatewayService.addNewRoutes(newRouteDtos, refresh);
    }

    @ApiOperation(value = "Xóa các routing", response = Iterable.class)
    @Responses(value = {
            @Response(responseValue = ResponseValue.SUCCESS, responseBody = ZuulRoutesSwagger.class)
    })
    @AuthorizationRequired
    @DeleteMapping("/routes")
    public BaseResponse deleteRoutes(@RequestParam(value = "refresh", defaultValue = "true") boolean refresh,
                                     @RequestBody Set<String> routeIDs) {
        return apiGatewayService.deleteRoutes(routeIDs, refresh);
    }

    @ApiOperation(value = "Refresh routing", response = Iterable.class)
    @Responses(value = {
            @Response(responseValue = ResponseValue.SUCCESS, responseBody = BaseResponseBody.class)
    })
    @PostMapping("/refresh")
    public BaseResponse refreshRoute() {
        return apiGatewayService.updateRoutes(true);
    }
}
