package com.spring.api_gateway.modules.gateway.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.api_gateway.modules.gateway.models.entities.Routing;

import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class NewRouteDto {
    @JsonProperty("id")
    @NotEmpty
    private String id;
    @JsonProperty("path")
    @NotEmpty
    private String path;
    @JsonProperty("location")
    @NotEmpty
    private String location;
    @JsonProperty("strip_prefix")
    private boolean stripPrefix = true;
    @JsonProperty("sensitive_headers")
    private Set<String> sensitiveHeaders = new HashSet<>();

    public NewRouteDto() {
    }

    public NewRouteDto(Routing routing) {
        setId(String.valueOf(routing.getId()));
        setPath(routing.getPath());
        setLocation(routing.getLocation());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public Set<String> getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(Set<String> sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }
}
