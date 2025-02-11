package com.rentify.type.service;

import com.rentify.base.exception.BadRequestException;
import com.rentify.base.exception.IdNotFoundException;
import com.rentify.type.dao.TypeDAO;
import com.rentify.type.dto.TypeDTO;
import com.rentify.type.dto.TypeRequestDTO;
import com.rentify.type.entity.Type;
import com.rentify.type.service.mapper.TypeMapper;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class TypeService {

    @Inject
    private TypeDAO typeDAO;

    @Inject
    private TypeMapper typeMapper;

    public List<TypeDTO> findAll() {
        return typeMapper.toDTOs(typeDAO.findAll().stream()
                .filter(type -> type.getDeletedAt() == null)
                .collect(Collectors.toList()));
    }

    public TypeDTO findById(Long typeId) {
        Type type = typeDAO.findById(typeId).orElseThrow(() -> new IdNotFoundException("Cannot found type with id: " + typeId));
        if (type.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Type with id %s is deleted before", typeId));
        }
        return typeMapper.toDTO(type);
    }

    public TypeDTO createType(TypeRequestDTO typeRequestDTO) {
        Type type = typeMapper.toEntity(typeRequestDTO);
        typeDAO.save(type);
        return typeMapper.toDTO(type);
    }

    public TypeDTO updateType(Long typeId, TypeRequestDTO typeUpdateDTO) {
        Type updateType = typeDAO.findById(typeId).orElseThrow(() -> new IdNotFoundException("Cannot found type with id: " + typeId));
        if (updateType.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Type with id %s is deleted before", typeId));
        }
        updateType.setName(typeUpdateDTO.getName());
        return typeMapper.toDTO(typeDAO.update(updateType));
    }

    public void deleteType(Long typeId) {
        Type deleteType = typeDAO.findById(typeId).orElseThrow(() -> new IdNotFoundException("Cannot found type with id: " + typeId));
        if (deleteType.getDeletedAt() != null) {
            throw new BadRequestException(String.format("Type with id %s is deleted before", typeId));
        }
        deleteType.setDeletedAt(LocalDateTime.now());
        typeDAO.update(deleteType);
    }

}
