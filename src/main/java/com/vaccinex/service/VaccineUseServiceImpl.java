package com.vaccinex.service;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dao.VaccineUseDao;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineUseCreateRequest;
import com.vaccinex.dto.request.VaccineUseUpdateRequest;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.mapper.VaccineUseMapper;
import com.vaccinex.pojo.VaccineUse;
import com.vaccinex.utils.VaccineUseSpecification;
import jakarta.ejb.Stateless;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class VaccineUseServiceImpl extends BaseServiceImpl<VaccineUse, Integer> implements VaccineUseService {

    private final VaccineUseDao vaccineUseRepository;
    private final VaccineUseMapper vaccineUseMapper;

    public VaccineUseServiceImpl(VaccineUseDao vaccineUseRepository, VaccineUseMapper vaccineUseMapper) {
        super(vaccineUseRepository);
        this.vaccineUseRepository = vaccineUseRepository;
        this.vaccineUseMapper = vaccineUseMapper;
    }

    @Override
    public PagingResponse getAllPurposes(Integer currentPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = vaccineUseRepository.findAll(pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách mục đích sử dụng vaccine với phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách mục đích sử dụng vaccine với phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    public PagingResponse getAllPurposesActive(Integer currentPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = vaccineUseRepository.findAllByDeletedFalse(pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách mục đích sử dụng vaccine đang hoạt động với phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách mục đích sử dụng vaccine đang hoạt động với phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    public List<VaccineUseResponseDTO> getPurposes() {
        return vaccineUseRepository.findAll().stream().map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<VaccineUseResponseDTO> getPurposesActive() {
        return vaccineUseRepository.findByDeletedIsFalse().stream().map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO).collect(Collectors.toList());
    }

    @Override
    public VaccineUseResponseDTO createPurpose(VaccineUseCreateRequest vaccineUseCreateRequest) {
        VaccineUse checkExist = vaccineUseRepository.findPurposeByName(vaccineUseCreateRequest.getName());
        if (checkExist != null) {
            throw new ElementExistException("Đã tồn tại một mục đích sử dụng vaccine với tên " + vaccineUseCreateRequest.getName());
        }
        VaccineUse vaccineUse = VaccineUse.builder()
                .name(vaccineUseCreateRequest.getName())
                .description(vaccineUseCreateRequest.getDescription())
                .build();
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUseRepository.save(vaccineUse));
    }

    @Override
    public VaccineUseResponseDTO updatePurpose(VaccineUseUpdateRequest vaccineUseUpdateRequest, int purposeID) {
        VaccineUse vaccineUse = vaccineUseRepository.findPurposeById(purposeID);
        if (vaccineUse != null) {
            if (vaccineUseUpdateRequest.getName() != null) {
                if (!vaccineUse.getName().equals(vaccineUseUpdateRequest.getName())) {
                    VaccineUse checkExist = vaccineUseRepository.findPurposeByName(vaccineUseUpdateRequest.getName());
                    if (checkExist != null) {
                        throw new ElementExistException("Tên mục đích sử dụng vaccine đã tồn tại với tên: \"" + vaccineUseUpdateRequest.getName() + "\"");
                    }
                }
                vaccineUse.setName(vaccineUseUpdateRequest.getName());
            }
            if (vaccineUseUpdateRequest.getDescription() != null) {
                vaccineUse.setDescription(vaccineUseUpdateRequest.getDescription());
            }
            return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUseRepository.save(vaccineUse));
        }
        return null;
    }

    @Override
    public VaccineUseResponseDTO undeletePurpose(Integer purposeID) {
        VaccineUse vaccineUse = vaccineUseRepository.findPurposeById(purposeID);
        if (vaccineUse == null) {
            throw new ElementNotFoundException("Không tìm thấy mục đích sử dụng vaccine");
        }
        if (!vaccineUse.isDeleted()) {
            throw new UnchangedStateException("Mục đích sử dụng vaccine chưa được xóa");
        }
        vaccineUse.setDeleted(false);
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUseRepository.save(vaccineUse));
    }

    @Override
    public VaccineUseResponseDTO deletePurpose(Integer purposeID) {
        VaccineUse vaccineUse = vaccineUseRepository.findPurposeById(purposeID);
        if (vaccineUse == null) {
            throw new ElementNotFoundException("Không tìm thấy mục đích sử dụng vaccine");
        }
        vaccineUse.setDeleted(true);
        return vaccineUseMapper.vaccineUseToVaccineUseResponseDTO(vaccineUseRepository.save(vaccineUse));
    }

    @Override
    public PagingResponse searchVaccineUses(Integer currentPage, Integer pageSize, String name, String sortBy) {
        Pageable pageable;

        Specification<VaccineUse> spec = Specification.where(null);

        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        String searchName = "";
        if (StringUtils.hasText(name)) {
            searchName = name;
        }
        keys.add("name");
        values.add(searchName);

        if(keys.size() == values.size()) {
            for(int i = 0; i < keys.size(); i++) {
                String field = keys.get(i);
                String value = values.get(i);
                Specification<VaccineUse> newSpec = VaccineUseSpecification.searchByField(field, value);
                if(newSpec != null) {
                    spec = spec.and(newSpec);
                }
            }
        }

        List<Sort.Order> orders = new ArrayList<>();
        if (StringUtils.hasText(sortBy)) {
            String sortByLower = sortBy.trim().toLowerCase();

            boolean hasNameASC = sortByLower.contains("nameasc");
            boolean hasNameDESC = sortByLower.contains("namedesc");

            if (hasNameASC ^ hasNameDESC) {
                orders.add(hasNameASC ? Sort.Order.asc("name") : Sort.Order.desc("name"));
            }

        }

        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);

        pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        var pageData = vaccineUseRepository.findAll(spec, pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách mục đích sử dụng vaccine với bộ lọc, sắp xếp và phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách mục đích sử dụng vaccine với bộ lọc, sắp xếp và phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(vaccineUseMapper::vaccineUseToVaccineUseResponseDTO)
                                .toList())
                        .build();
    }

}
