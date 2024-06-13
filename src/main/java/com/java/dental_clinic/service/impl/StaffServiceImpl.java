package com.java.dental_clinic.service.impl;

import com.java.dental_clinic.data.dto.MessageResponse;
import com.java.dental_clinic.data.dto.PaginationDTO;
import com.java.dental_clinic.data.dto.staff.StaffCreationDTO;
import com.java.dental_clinic.data.dto.staff.StaffDTO;
import com.java.dental_clinic.data.entity.Position;
import com.java.dental_clinic.data.entity.Staff;
import com.java.dental_clinic.data.entity.User;
import com.java.dental_clinic.data.enumeration.ERole;
import com.java.dental_clinic.data.maper.StaffMapper;
import com.java.dental_clinic.exception.ResourceNotFoundException;
import com.java.dental_clinic.repostiory.PositionRepository;
import com.java.dental_clinic.repostiory.StaffRepository;
import com.java.dental_clinic.service.StaffService;
import com.java.dental_clinic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Service
public class StaffServiceImpl implements StaffService {
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private PositionRepository positionRepository;
    @Autowired
    private UserService userService;

    @Override
    public MessageResponse createStaff(StaffCreationDTO staffCreationDTO) {

        Staff staff = staffMapper.toEntity(staffCreationDTO.getStaffDTO());
        Position position = positionRepository.findById(staffCreationDTO.getPositionId()).orElseThrow(
                () -> new ResourceNotFoundException(
                        Collections.singletonMap("position id: ", staffCreationDTO.getPositionId()))
        );

        User user = userService.createUser(staffCreationDTO.getLoginDTO(), ERole.roleStaff);

        staff.setUser(user);
        staff.setPosition(position);

        staffRepository.save(staff);

        return new MessageResponse(HttpServletResponse.SC_CREATED, "successfully");
    }

    @Override
    public MessageResponse updateStaff(StaffDTO staffDTO, Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("id: ", id))
        );

        Staff update = staffMapper.toEntity(staffDTO);
        update.setPosition(staff.getPosition());
        update.setUser(staff.getUser());
        update.setId(id);

        staffRepository.save(update);

        return new MessageResponse(HttpServletResponse.SC_OK, "successfully");
    }


    @Override
    public PaginationDTO filterStaff(String keyword, int pageNumber, int pageSize, Long positionId) {
        Page<StaffDTO> page = staffRepository.filter(keyword, PageRequest.of(pageNumber, pageSize), positionId)
                .map(staff -> staffMapper.toDTO(staff));

        return new PaginationDTO(page.getContent(), page.isFirst(), page.isLast(),
                page.getTotalPages(), page.getTotalElements(), page.getNumber(), page.getSize());
    }

    @Override
    public Staff getStaffByToken() {
        User user = userService.getUserByToken();

        Staff staff = staffRepository.findByUserId(user.getId()).orElseThrow(
                () -> new ResourceNotFoundException(Collections.singletonMap("message: ", "user isn't existed"))
        );

        return staff;
    }
}
