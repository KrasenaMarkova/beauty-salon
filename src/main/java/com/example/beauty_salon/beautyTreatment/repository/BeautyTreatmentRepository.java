package com.example.beauty_salon.beautyTreatment.repository;

import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.model.BeautyTreatmentName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BeautyTreatmentRepository extends JpaRepository<BeautyTreatment, UUID> {

    Optional<BeautyTreatment> findByBeautyTreatmentName(BeautyTreatmentName beautyTreatmentName);

}

