package com.java.dental_clinic.data.maper;

import com.java.dental_clinic.data.dto.InvoiceDTO;
import com.java.dental_clinic.data.entity.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ObjectiveMapper.class})
public interface InvoiceMapper {
    @Mapping(source = "objective", target = "objectiveDTO")
    InvoiceDTO toDTO(Invoice invoice);
}
