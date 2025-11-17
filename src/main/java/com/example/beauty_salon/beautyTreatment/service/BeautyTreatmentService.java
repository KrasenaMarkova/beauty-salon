package com.example.beauty_salon.beautyTreatment.service;

import com.example.beauty_salon.beautyTreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautyTreatment.repository.BeautyTreatmentRepository;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeautyTreatmentService {

    private final BeautyTreatmentRepository beautyTreatmentRepository;

    @Autowired
    public BeautyTreatmentService(BeautyTreatmentRepository beautyTreatmentRepository) {
        this.beautyTreatmentRepository = beautyTreatmentRepository;
    }

    public List<BeautyTreatment> getAll() {
        return beautyTreatmentRepository.findAll();
    }

    public BeautyTreatment getById(UUID id) {
        return beautyTreatmentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Услугата не е намерена."));
    }
}
