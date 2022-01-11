package com.spring.api_gateway.modules.gateway.repositories;

import com.spring.api_gateway.modules.gateway.models.entities.Routing;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessRouteRepository extends JpaRepository<Routing, Integer> {
}
