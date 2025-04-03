package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.RoleRequestDTO;
import com.sba301.vaccinex.dto.response.RoleResponseDTO;
import com.sba301.vaccinex.exception.ElementNotFoundException;
import com.sba301.vaccinex.exception.ParseEnumException;
import com.sba301.vaccinex.mapper.RoleMapper;
import com.sba301.vaccinex.pojo.Role;
import com.sba301.vaccinex.pojo.enums.EnumRoleNameType;
import com.sba301.vaccinex.repository.RoleRepository;
import com.sba301.vaccinex.service.spec.RoleService;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Stateless
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<RoleResponseDTO> findAll() {
        return RoleMapper.INSTANCE.toDTOs(roleRepository.findAllByDeletedIsFalse());
    }

    @Override
    public RoleResponseDTO findById(Integer roleId) {
        Role role = roleRepository.findByIdAndDeletedIsFalse(roleId)
                .orElseThrow(() -> new ElementNotFoundException("Không tìm thấy vai trò có id: " + roleId));
        return RoleMapper.INSTANCE.toDTO(role);
    }

    @Override
public RoleResponseDTO createRole(RoleRequestDTO dto) throws ParseEnumException {
    EnumRoleNameType roleName;
    try {
        roleName = EnumRoleNameType.valueOf(dto.getName().toUpperCase());
    } catch (IllegalArgumentException e) {
        throw new ParseEnumException("Tên vai trò không hợp lệ: " + dto.getName());
    }

    if (roleRepository.getRoleByRoleName(roleName) != null) {
        throw new ParseEnumException("Vai trò có tên " + dto.getName() + " đã tồn tại");
    }

    Role role = RoleMapper.INSTANCE.toEntity(dto);
    role.setRoleName(roleName);
    return RoleMapper.INSTANCE.toDTO(roleRepository.save(role));
}

    @Override
    public RoleResponseDTO update(Integer roleId, RoleRequestDTO dto) {
        Role role = roleRepository.findByIdAndDeletedIsFalse(roleId)
                .orElseThrow(() -> new ElementNotFoundException("Không tìm thấy vai trò có id: " + roleId));

        try {
            role.setRoleName(EnumRoleNameType.valueOf(dto.getName().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tên vai trò không hợp lệ: " + dto.getName());
        }

        role.setDisplayName(dto.getDisplayName());

        return RoleMapper.INSTANCE.toDTO(roleRepository.save(role));
    }

    @Override
    public void deleteById(Integer roleId) {
        Role role = roleRepository.findByIdAndDeletedIsFalse(roleId)
                .orElseThrow(() -> new ElementNotFoundException("Không tìm thấy vai trò có id: " + roleId));

        role.setDeleted(true);
        roleRepository.save(role);
    }

    @Override
    public Role getRoleByRoleName(EnumRoleNameType roleName) {
        return Optional.ofNullable(roleRepository.getRoleByRoleName(roleName))
                .orElseThrow(() -> new ElementNotFoundException("Không tìm thấy vai trò"));
    }

}
