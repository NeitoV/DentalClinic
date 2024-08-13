package com.java.dental_clinic.service.impl;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.ObjectiveDTO;
import com.java.dental_clinic.data.dto.ObjectivesCreationDTO;
import com.java.dental_clinic.data.dto.PaginationDTO;
import com.java.dental_clinic.data.entity.MedicalRecord;
import com.java.dental_clinic.data.entity.Objective;
import com.java.dental_clinic.data.entity.TherapyProcedure;
import com.java.dental_clinic.data.entity.User;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.enumeration.EStatus;
import com.java.dental_clinic.data.maper.ObjectiveMapper;
import com.java.dental_clinic.exception.AccessDeniedException;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.InvoiceRepository;
import com.java.dental_clinic.repostiory.ObjectiveRepository;
import com.java.dental_clinic.repostiory.ProcedureRepository;
import com.java.dental_clinic.repostiory.RecordRepository;
import com.java.dental_clinic.service.MedicalRecordService;
import com.java.dental_clinic.service.ObjectiveService;
import com.java.dental_clinic.service.UserService;
import com.java.dental_clinic.util.PDFUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Service
public class ObjectiveServiceImpl implements ObjectiveService {
    @Autowired
    private RecordRepository recordRepository;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private ObjectiveRepository objectiveRepository;
    @Autowired
    private ProcedureRepository procedureRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ObjectiveMapper objectiveMapper;
    @Autowired
    private PDFUtils pdfUtils;
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public MessageResponse createObjectiveForRecord(Long recordId, List<ObjectivesCreationDTO> list) {
        MedicalRecord medicalRecord = recordRepository.findById(recordId).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("record id: ", recordId))
        );

        userService.checkStaff(medicalRecord.getStaff().getId());

        if (medicalRecord.getStatus().equals(EStatus.DONE.toString())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update record done"));
        }

        medicalRecordService.createListObjectives(list, medicalRecord);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse deleteObjective(Long id) {
        Objective objective = objectiveRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", id))
        );

        if (objective.getMedicalRecord().getStatus().equals(EStatus.DONE.toString())) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't update record done"));
        }

        if (invoiceRepository.existsByObjectiveId(id)) {
            throw new AccessDeniedException(Collections.singletonMap("message", "the customer have been paid for the obj"));
        }

        userService.checkStaff(objective.getMedicalRecord().getStaff().getId());

        procedureRepository.deleteAllByObjectiveId(id);
        objectiveRepository.deleteById(id);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }

    @Override
    public PaginationDTO findAllByRecordId(Long recordId, int pageNumber, int pageSize, Long objectiveId) {

        Page<ObjectiveDTO> page = objectiveRepository.findAllByMedicalRecordIdAndObjectiveId(recordId, objectiveId,
                        PageRequest.of(pageNumber, pageSize))
                .map(objective -> objectiveMapper.toDTO(objective));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public ByteArrayResource createPdfObjective(Long objectiveId) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<TherapyProcedure> procedureList = procedureRepository.findAllByObjectiveId(objectiveId);

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, out);

            document.open();
            BaseFont baseFont = pdfUtils.loadBaseFont();

            pdfUtils.addObjectiveContent(document, baseFont, procedureList);
            document.close();

        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ByteArrayResource(out.toByteArray());
    }

    @Override
    public ObjectiveDTO findById(Long id) {
        Objective objective = objectiveRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", id))
        );

        User user = userService.getUserByToken();
        if(user.getRole().getId() == ERole.rolePatient && objective.getMedicalRecord().getPatient().getUser().getId() != user.getId()) {
            throw new AccessDeniedException(Collections.singletonMap("message", "can't access"));
        }

        return objectiveMapper.toDTO(objective);
    }


}
