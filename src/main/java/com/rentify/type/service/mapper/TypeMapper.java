package com.rentify.type.service.mapper;

import com.rentify.type.dto.TypeDTO;
import com.rentify.type.dto.TypeRequestDTO;
import com.rentify.type.entity.Type;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "cdi")
public interface TypeMapper {
    TypeDTO toDTO(Type type);
    Type toEntity(TypeRequestDTO typeRequestDTO);
    List<TypeDTO> toDTOs(List<Type> types);
}
