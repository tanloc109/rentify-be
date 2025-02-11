package com.rentify.brand.service;

import com.rentify.base.exception.BadRequestException;
import com.rentify.base.exception.IdNotFoundException;
import com.rentify.brand.dao.BrandDAO;
import com.rentify.brand.dto.BrandDTO;
import com.rentify.brand.dto.BrandRequestDTO;
import com.rentify.brand.entity.Brand;
import com.rentify.brand.service.mapper.BrandMapper;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class BrandService {

    @Inject
    private BrandDAO brandDAO;

    @Inject
    private BrandMapper brandMapper;

    public List<BrandDTO> findAll() {
        return brandMapper.toDTOs(brandDAO.findAll().stream()
                .filter(brand -> brand.getDeletedAt() == null)
                .collect(Collectors.toList()));
    }

    public BrandDTO findById(Long brandId) {
        Brand brand = brandDAO.findById(brandId).orElseThrow(() -> new IdNotFoundException("Cannot found brand with id: " + brandId));
        if (brand.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Brand with id %s is deleted before", brandId));
        }
        return brandMapper.toDTO(brand);
    }

    public BrandDTO createBrand(BrandRequestDTO brandRequestDTO) {
        Brand brand = brandMapper.toEntity(brandRequestDTO);
        brandDAO.save(brand);
        return brandMapper.toDTO(brand);
    }

    public BrandDTO updateBrand(Long brandId, BrandRequestDTO brandUpdateDTO) {
        Brand updateBrand = brandDAO.findById(brandId).orElseThrow(() -> new IdNotFoundException("Cannot found brand with id: " + brandId));
        if (updateBrand.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Brand with id %s is deleted before", brandId));
        }
        updateBrand.setName(brandUpdateDTO.getName());
        return brandMapper.toDTO(brandDAO.update(updateBrand));
    }

    public void deleteBrand(Long brandId) {
        Brand deleteBrand = brandDAO.findById(brandId).orElseThrow(() -> new IdNotFoundException("Cannot found brand with id: " + brandId));
        if (deleteBrand.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Brand with id %s is deleted before", brandId));
        }
        deleteBrand.setDeletedAt(LocalDateTime.now());
        brandDAO.update(deleteBrand);
    }

}
