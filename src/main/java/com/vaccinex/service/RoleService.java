package com.vaccinex.service;

import com.vaccinex.base.exception.ParseEnumException;
import com.vaccinex.dto.request.RoleRequestDTO;
import com.vaccinex.dto.response.RoleResponseDTO;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.enums.EnumRoleNameType;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> findAll();
    RoleResponseDTO findById(Integer roleId);
    RoleResponseDTO createRole(RoleRequestDTO dto) throws ParseEnumException;
    RoleResponseDTO update(Integer roleId, RoleRequestDTO dto);
    void deleteById(Integer roleId);
    Role getRoleByRoleName(EnumRoleNameType enumRoleNameType);
}
