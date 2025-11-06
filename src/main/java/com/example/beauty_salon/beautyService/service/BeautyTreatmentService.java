package com.example.beauty_salon.beautyService.service;

import com.example.beauty_salon.beautyService.repository.BeautyTreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BeautyTreatmentService {

    private final BeautyTreatmentRepository beautyTreatmentRepository;

    @Autowired
    public BeautyTreatmentService(BeautyTreatmentRepository beautyTreatmentRepository) {
        this.beautyTreatmentRepository = beautyTreatmentRepository;
    }
}
