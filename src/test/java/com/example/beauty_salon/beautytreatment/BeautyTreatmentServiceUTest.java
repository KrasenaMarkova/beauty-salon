package com.example.beauty_salon.beautytreatment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.beauty_salon.beautytreatment.model.BeautyTreatment;
import com.example.beauty_salon.beautytreatment.model.BeautyTreatmentName;
import com.example.beauty_salon.beautytreatment.repository.BeautyTreatmentRepository;
import com.example.beauty_salon.beautytreatment.service.BeautyTreatmentService;
import com.example.beauty_salon.web.dto.EditBeautyTreatmentRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BeautyTreatmentServiceUTest {

  @Mock
  private BeautyTreatmentRepository beautyTreatmentRepository;

  @InjectMocks
  private BeautyTreatmentService beautyTreatmentService;

  private UUID treatmentId;

  @BeforeEach
  void setUp() {
    treatmentId = UUID.randomUUID();
  }

  @Test
  void whenAdjustPricesForInflation_thenPricesUpdatedCorrectly() {

    BeautyTreatment treatment1 = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .serviceDescription("Haircut")
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .price(BigDecimal.valueOf(50.00))
        .durationMinutes(60)
        .build();

    BeautyTreatment treatment2 = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .serviceDescription("Manicure")
        .beautyTreatmentName(BeautyTreatmentName.MANICURE)
        .price(BigDecimal.valueOf(30.00))
        .durationMinutes(45)
        .build();

    when(beautyTreatmentRepository.findAll()).thenReturn(List.of(treatment1, treatment2));

    beautyTreatmentService.adjustPricesForInflation();

    BigDecimal expectedPrice1 = BigDecimal.valueOf(50.00)
        .multiply(new BigDecimal("1.008"))
        .setScale(2, RoundingMode.HALF_UP);
    BigDecimal expectedPrice2 = BigDecimal.valueOf(30.00)
        .multiply(new BigDecimal("1.008"))
        .setScale(2, RoundingMode.HALF_UP);

    assertEquals(expectedPrice1, treatment1.getPrice());
    assertEquals(expectedPrice2, treatment2.getPrice());
  }

  @Test
  void whenGetAll_thenReturnListFromRepository() {
    BeautyTreatment t1 = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .serviceDescription("Haircut")
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .price(BigDecimal.valueOf(50))
        .durationMinutes(60)
        .build();

    BeautyTreatment t2 = BeautyTreatment.builder()
        .id(UUID.randomUUID())
        .serviceDescription("Manicure")
        .beautyTreatmentName(BeautyTreatmentName.MANICURE)
        .price(BigDecimal.valueOf(30))
        .durationMinutes(45)
        .build();

    when(beautyTreatmentRepository.findAll()).thenReturn(List.of(t1, t2));

    List<BeautyTreatment> result = beautyTreatmentService.getAll();

    assertEquals(2, result.size());
    assertTrue(result.contains(t1));
    assertTrue(result.contains(t2));
  }

  @Test
  void whenGetById_andExists_thenReturnTreatment() {
    BeautyTreatment treatment = BeautyTreatment.builder()
        .id(treatmentId)
        .serviceDescription("Haircut")
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .price(BigDecimal.valueOf(50))
        .durationMinutes(60)
        .build();

    when(beautyTreatmentRepository.findById(treatmentId)).thenReturn(Optional.of(treatment));

    BeautyTreatment result = beautyTreatmentService.getById(treatmentId);

    assertEquals(treatmentId, result.getId());
    assertEquals("Haircut", result.getServiceDescription());
  }

  @Test
  void whenGetById_andDoesNotExist_thenThrowsException() {
    when(beautyTreatmentRepository.findById(treatmentId)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> beautyTreatmentService.getById(treatmentId));

    assertEquals("Услугата не е намерена.", ex.getMessage());
  }

  @Test
  void whenUpdate_thenModifyServiceDescriptionAndSave() {
    BeautyTreatment treatment = BeautyTreatment.builder()
        .id(treatmentId)
        .serviceDescription("Old Description")
        .beautyTreatmentName(BeautyTreatmentName.HAIRCUT)
        .price(BigDecimal.valueOf(50))
        .durationMinutes(60)
        .build();

    EditBeautyTreatmentRequest editRequest = new EditBeautyTreatmentRequest();
    editRequest.setServiceDescription("New Description");

    when(beautyTreatmentRepository.findById(treatmentId)).thenReturn(Optional.of(treatment));

    beautyTreatmentService.update(treatmentId, editRequest);

    assertEquals("New Description", treatment.getServiceDescription());
    verify(beautyTreatmentRepository).save(treatment);
  }

  @Test
  void whenUpdate_andTreatmentNotFound_thenThrowsException() {
    EditBeautyTreatmentRequest editRequest = new EditBeautyTreatmentRequest();
    editRequest.setServiceDescription("New Description");

    when(beautyTreatmentRepository.findById(treatmentId)).thenReturn(Optional.empty());

    IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
        () -> beautyTreatmentService.update(treatmentId, editRequest));

    assertEquals("Услугата не съществува", ex.getMessage());
  }
}
