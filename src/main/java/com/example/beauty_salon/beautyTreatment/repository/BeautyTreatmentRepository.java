package com.example.beauty_salon.beautyTreatment.repository;

import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeautyTreatmentRepository extends JpaRepository<BeautyTreatment, UUID> {

}

