package com.rentify.brand.service.mapper;

import com.rentify.brand.dto.BrandDTO;
import com.rentify.brand.dto.BrandRequestDTO;
import com.rentify.brand.entity.Brand;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface BrandMapper {
    BrandDTO toDTO(Brand brand);
    Brand toEntity(BrandRequestDTO brandRequestDTO);
    List<BrandDTO> toDTOs(List<Brand> brands);
}
