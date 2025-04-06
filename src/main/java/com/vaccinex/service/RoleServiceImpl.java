package com.vaccinex.service;

import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.ParseEnumException;
import com.vaccinex.dao.RoleDao;
import com.vaccinex.dto.request.RoleRequestDTO;
import com.vaccinex.dto.response.RoleResponseDTO;
import com.vaccinex.mapper.RoleMapper;
import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.enums.EnumRoleNameType;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Stateless
public class RoleServiceImpl implements RoleService {

    @Inject
    private RoleDao roleRepository;

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
