package com.example.beauty_salon.beautyService.repository;

import com.example.beauty_salon.beautyService.model.BeautyService;
import com.example.beauty_salon.beautyService.model.ServiceName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeautyServiceRepository extends JpaRepository<BeautyService, UUID> {

    Optional<BeautyService> findByServiceName(ServiceName serviceName);

}

