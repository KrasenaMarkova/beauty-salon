package com.example.beauty_salon.beautytreatment.service;

import com.example.beauty_salon.beautytreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautytreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.web.dto.EditBeautyTreatmentRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BeautyTreatmentService {

  private static final BigDecimal MONTHLY_INFLATION_FACTOR = BigDecimal.valueOf(1.008);

  private final BeautyTreatmentRepository beautyTreatmentRepository;

  @Autowired
  public BeautyTreatmentService(BeautyTreatmentRepository beautyTreatmentRepository) {
    this.beautyTreatmentRepository = beautyTreatmentRepository;
  }

  @Transactional
  @CacheEvict(value = "beautyTreatments", allEntries = true)
  public void adjustPricesForInflation() {

    List<BeautyTreatment> treatments = beautyTreatmentRepository.findAll();

    for (BeautyTreatment treatment : treatments) {
      BigDecimal oldPrice = treatment.getPrice();
      BigDecimal newPrice = oldPrice.multiply(MONTHLY_INFLATION_FACTOR)
          .setScale(2, RoundingMode.HALF_UP);
      treatment.setPrice(newPrice);
    }

    System.out.println("Updated prices for " + treatments.size() +
        " treatments due to 0.8% monthly inflation.");
  }

  @Cacheable("beautyTreatments")
  public List<BeautyTreatment> getAll() {
    return beautyTreatmentRepository.findAll();
  }

  public BeautyTreatment getById(UUID id) {
    return beautyTreatmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Услугата не е намерена."));
  }

  @Transactional
  @CacheEvict(value = "beautyTreatments", allEntries = true)
  public void update(UUID id, @Valid EditBeautyTreatmentRequest editBeautyTreatmentRequest) {

    BeautyTreatment treatment = beautyTreatmentRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Услугата не съществува"));

    treatment.setServiceDescription(editBeautyTreatmentRequest.getServiceDescription());

    beautyTreatmentRepository.save(treatment);
  }
}
