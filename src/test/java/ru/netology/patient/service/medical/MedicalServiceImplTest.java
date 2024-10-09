package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoFileRepository;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;
import ru.netology.patient.service.alert.SendAlertServiceImpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;

@DisplayName("MedicalServiceImpl Test")
class MedicalServiceImplTest {
    public static Stream<Arguments> idAndPressure () {
        return Stream.of(
                Arguments.of("13425",new BloodPressure(120,80)),
                Arguments.of("13427",new BloodPressure(140,80))
        );
    }
    @ParameterizedTest
    @MethodSource("idAndPressure")
    @DisplayName("Method Testing: checkBloodPressure")
    void checkBloodPressureTest (String id, BloodPressure bloodPressure) {
        PatientInfoFileRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById(id))
                .thenReturn(new PatientInfo(id,"Иван", "Петров", LocalDate.of(1980, 11,26),
                        new HealthInfo(new BigDecimal("36.6"),new BloodPressure(120,80))));

        String expected = String.format("Warning, patient with id: %s, need help", id);
        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository,alertService);
        medicalService.checkBloodPressure(id,bloodPressure);
        Mockito.verify(alertService,Mockito.atMostOnce()).send(expected);
    }

    public static Stream<Arguments> idAndTemprature (){
        return Stream.of(
                Arguments.of("777", new BigDecimal("36.6")),
                Arguments.of("775", new BigDecimal("38.0"))
        );
    }

    @ParameterizedTest
    @MethodSource("idAndTemprature")
    @DisplayName("Method Testing: checkTemperature ")
    void checkTemperature(String id, BigDecimal temp) {
        PatientInfoFileRepository patientInfoRepository = Mockito.mock(PatientInfoFileRepository.class);
        Mockito.when(patientInfoRepository.getById("777"))
                .thenReturn(new PatientInfo("777","Василий", "Перфильев", LocalDate.of(1984, 7,2),
                        new HealthInfo(new BigDecimal("36.6"),new BloodPressure(120,80))));
        Mockito.when(patientInfoRepository.getById("775"))
                .thenReturn(new PatientInfo("775","Андрий", "Темнов", LocalDate.of(1981, 10,22),
                        new HealthInfo(new BigDecimal("36.6"),new BloodPressure(125,90))));
        SendAlertService alertService = Mockito.mock(SendAlertServiceImpl.class);
        MedicalService medicalService = new MedicalServiceImpl(patientInfoRepository,alertService);
        String expected = String.format("Warning, patient with id: %s, need help", id);
        medicalService.checkTemperature(id,temp);
        Mockito.verify(alertService,Mockito.atMostOnce()).send(expected);
    }
}