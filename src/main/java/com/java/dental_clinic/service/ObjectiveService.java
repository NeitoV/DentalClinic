package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectiveDTO;
import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ObjectiveService {
    MessageResponse createObjectiveForRecord(Long recordId, List<ObjectivesCreationDTO> list);

    @Transactional
    MessageResponse deleteObjective(Long id);

    List<ObjectiveDTO> findAllByRecordId(Long recordId);
}
