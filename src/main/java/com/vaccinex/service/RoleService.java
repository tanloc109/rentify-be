package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.RoleRequestDTO;
import com.sba301.vaccinex.dto.response.RoleResponseDTO;
import com.sba301.vaccinex.exception.ParseEnumException;
import com.sba301.vaccinex.pojo.Role;
import com.sba301.vaccinex.pojo.enums.EnumRoleNameType;

import java.util.List;

public interface RoleService {
    List<RoleResponseDTO> findAll();
    RoleResponseDTO findById(Integer roleId);
    RoleResponseDTO createRole(RoleRequestDTO dto) throws ParseEnumException;
    RoleResponseDTO update(Integer roleId, RoleRequestDTO dto);
    void deleteById(Integer roleId);
    Role getRoleByRoleName(EnumRoleNameType enumRoleNameType);
}
