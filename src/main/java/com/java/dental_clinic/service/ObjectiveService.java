package com.java.dental_clinic.service;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectiveDTO;
import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import com.java.dental_clinic.data.dto.PaginationDTO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ObjectiveService {
    MessageResponse createObjectiveForRecord(Long recordId, List<ObjectivesCreationDTO> list);

    @Transactional
    MessageResponse deleteObjective(Long id);

    PaginationDTO findAllByRecordId(Long recordId, int pageNumber, int pageSize, Long objectiveId);

    ByteArrayResource createPdfObjective(Long objectiveId);

    ObjectiveDTO findById(Long id);
}
