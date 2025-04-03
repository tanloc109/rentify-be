package com.vaccinex.service;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dao.ComboDao;
import com.vaccinex.dto.paging.ComboPagingResponse;
import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineComboCreateRequest;
import com.vaccinex.dto.request.VaccineComboUpdateRequest;
import com.vaccinex.dto.response.ComboResponseDTO;
import com.vaccinex.dto.response.VaccineComboResponseDTO;
import com.vaccinex.mapper.ComboMapper;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.Combo;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineCombo;
import com.vaccinex.pojo.VaccineTiming;
import com.vaccinex.pojo.composite.VaccineComboId;
import com.vaccinex.utils.PaginationUtil;
import com.vaccinex.utils.VaccineComboSpecification;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;


@Stateless
@RequiredArgsConstructor
public class ComboServiceImpl extends BaseServiceImpl<Combo, Integer> implements ComboService {

    private final ComboDao comboRepository;
    private final ComboMapper comboMapper;
    private final VaccineMapper vaccineMapper;

    @Value("${price.combo.below}")
    private String below;

    @Value("${price.combo.higher}")
    private String higher;

    @Value("${price.combo.avg-begin}")
    private String avgBegin;

    @Value("${price.combo.avg-end}")
    private String avgEnd;

    @Value("${price.combo.default}")
    private String priceDefault;

    @Value("${business.interval-after-active-vaccine}")
    private int businessIntervalAfterActiveVaccine;

    @Value("${business.interval-after-inactive-vaccine}")
    private int businessIntervalAfterInactiveVaccine;

    @Override
    public MappingJacksonValue getAllCombosV2(PagingRequest request) {
        Pageable pageable = PaginationUtil.getPageable(request);
        Page<Combo> combos = comboRepository.findByDeletedIsFalse(pageable);
        List<ComboPagingResponse> mappedDTOList = combos.getContent().stream().map(ComboPagingResponse::fromEntity).toList();
        return PaginationUtil.getPagedMappingJacksonValue(request, combos, mappedDTOList, "Lấy tất cả combo thành công");
    }

    @Override
    public Combo getComboByIdV2(Integer id) {
        return comboRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new RuntimeException("Không tìm thấy combo với id: " + id)
        );
    }

    @Override
    public Combo getComboById(Integer id) {
        return comboRepository.findByIdAndDeletedIsFalse(id).orElseThrow(
                () -> new RuntimeException("Không tìm thấy combo với id: " + id)
        );
    }

    @Override
    public PagingResponse getAllCombos(Integer currentPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = comboRepository.findAll(pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách combo với phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(comboMapper::comboToComboResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách combo với phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(comboMapper::comboToComboResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    public PagingResponse getAllCombosActive(Integer currentPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = comboRepository.findAllByDeletedFalse(pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách combo đang hoạt động với phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(comboMapper::comboToComboResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách combo đang hoạt động với phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(comboMapper::comboToComboResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    public PagingResponse searchVaccineCombos(Integer currentPage, Integer pageSize, String name, String price, Integer minAge, Integer maxAge, String sortBy) {
        Pageable pageable;

        Specification<Combo> spec = Specification.where(null);

        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        String searchName = "";
        if (StringUtils.hasText(name)) {
            searchName = name;
        }
        keys.add("name");
        values.add(searchName);

        String searchPriceBegin = priceDefault;
        String searchPriceEnd = String.valueOf(comboRepository.getMaxPrice());
        if (StringUtils.hasText(price)) {
            switch (price.trim().toLowerCase()) {
                case "thấp" -> searchPriceEnd = below;
                case "cao" -> searchPriceBegin = higher;
                case "trung bình" -> {
                    searchPriceEnd = avgEnd;
                    searchPriceBegin = avgBegin;
                }
            }
        }

        keys.add("priceBegin");
        keys.add("priceEnd");
        values.add(searchPriceBegin);
        values.add(searchPriceEnd);

        String searchMinAge;
        comboRepository.getMaxAge();
        String searchMaxAge;
        if (minAge != 0 && maxAge == 0) {
            searchMinAge = String.valueOf(minAge);
            keys.add("ageBegin");
            values.add(searchMinAge);
        } else if (minAge == 0 && maxAge != 0) {
            searchMaxAge = String.valueOf(maxAge);
            keys.add("ageEnd");
            values.add(searchMaxAge);
        } else if (minAge != 0 && maxAge != 0) {
            String ageRange = minAge + "," + maxAge;

            keys.add("ageRange");
            values.add(ageRange);
        }

        if(keys.size() == values.size()) {
            for(int i = 0; i < keys.size(); i++) {
                String field = keys.get(i);
                String value = values.get(i);
                Specification<Combo> newSpec = VaccineComboSpecification.searchByField(field, value);
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
            boolean hasPriceASC = sortByLower.contains("priceasc");
            boolean hasPriceDESC = sortByLower.contains("pricedesc");
            boolean hasMinAgeASC = sortByLower.contains("ageminasc");
            boolean hasMinAgeDESC = sortByLower.contains("agemindesc");
            boolean hasMaxAgeDESC = sortByLower.contains("agemaxdesc");
            boolean hasMaxAgeASC = sortByLower.contains("agemaxasc");

            if (hasNameASC ^ hasNameDESC) {
                orders.add(hasNameASC ? Sort.Order.asc("name") : Sort.Order.desc("name"));
            }

            if (hasPriceASC ^ hasPriceDESC) {
                orders.add(hasPriceASC ? Sort.Order.asc("price") : Sort.Order.desc("price"));
            }

            if (hasMinAgeASC ^ hasMinAgeDESC) {
                orders.add(hasMinAgeASC ? Sort.Order.asc("minAge") : Sort.Order.desc("minAge"));
            }

            if (hasMaxAgeASC ^ hasMaxAgeDESC) {
                orders.add(hasMaxAgeASC ? Sort.Order.asc("maxAge") : Sort.Order.desc("maxAge"));
            }

        }

        Sort sort = orders.isEmpty() ? Sort.unsorted() : Sort.by(orders);

        pageable = PageRequest.of(currentPage - 1, pageSize, sort);

        var pageData = comboRepository.findAll(spec, pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách combo với bộ lọc, sắp xếp và phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(comboMapper::comboToComboResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy tất cả bộ lọc kết hợp và sắp xếp phân trang thất bại")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(comboMapper::comboToComboResponseDTO)
                                .toList())
                        .build();
    }

    @Transactional
    @Override
    public ComboResponseDTO createCombo(VaccineComboCreateRequest vaccineComboCreateRequest) {
        Combo checkExist = comboRepository.findComboByName(vaccineComboCreateRequest.getName());
        if (checkExist != null) {
            throw new ElementExistException("Gói tiêm đã tồn tại với tên là: " + vaccineComboCreateRequest.getName());
        }

        if (vaccineComboCreateRequest.getMinAge() > vaccineComboCreateRequest.getMaxAge()) {
            throw new BadRequestException("Tuổi nhỏ nhất phải nhỏ hơn tuổi lớn nhất");
        }

        if (vaccineComboCreateRequest.getVaccines() == null || vaccineComboCreateRequest.getVaccines().isEmpty()) {
            throw new BadRequestException("Phải có ít nhất 1 vaccine bên trong gói tiêm");
        }

        Combo combo = Combo.builder()
                .name(vaccineComboCreateRequest.getName())
                .description(vaccineComboCreateRequest.getDescription())
                .minAge(vaccineComboCreateRequest.getMinAge())
                .maxAge(vaccineComboCreateRequest.getMaxAge())
                .price(vaccineComboCreateRequest.getPrice())
                .build();

        int comboId = comboRepository.save(combo).getId();

        List<VaccineCombo> updatedVaccineCombo = new ArrayList<>();

        VaccineComboResponseDTO previousVaccineCombo = null;

        for (VaccineComboResponseDTO vaccineComboResponseDTO : vaccineComboCreateRequest.getVaccines()) {

            Vaccine vaccine = vaccineMapper.vaccineResponseDTOToVaccine(vaccineComboResponseDTO.getVaccine());

            VaccineComboId vaccineComboId = new VaccineComboId(vaccineComboResponseDTO.getId().getVaccineId(), comboId, vaccineComboResponseDTO.getId().getOrderInCombo());

            if (previousVaccineCombo != null) {
                boolean isCurrentActivated = previousVaccineCombo.getVaccine().isActivated();
                int requiredInterval = isCurrentActivated ? businessIntervalAfterActiveVaccine : businessIntervalAfterInactiveVaccine;
                long actualInterval = vaccineComboResponseDTO.getIntervalDays();

                if (actualInterval < requiredInterval) {
                    throw new BadRequestException(vaccine.getName() + " phải cách " + previousVaccineCombo.getVaccine().getName() + " ít nhất là " + requiredInterval + " ngày");
                }
            }

            VaccineCombo vaccineCombo = VaccineCombo.builder()
                    .id(vaccineComboId)
                    .combo(combo)
                    .vaccine(vaccine)
                    .intervalDays(vaccineComboResponseDTO.getIntervalDays())
                    .build();
            updatedVaccineCombo.add(vaccineCombo);

            previousVaccineCombo = vaccineComboResponseDTO;
        }
        combo.setVaccineCombos(updatedVaccineCombo);

        return comboMapper.comboToComboResponseDTO(comboRepository.save(combo));
    }

    @Transactional
    @Override
    public ComboResponseDTO updateCombo(VaccineComboUpdateRequest vaccineComboUpdateRequest, int comboID) {
        Combo combo = comboRepository.findComboById(comboID);
        if (combo == null) {
            throw new ElementNotFoundException("Không tìm thấy gói tiêm");
        }

        if (vaccineComboUpdateRequest.getName() != null) {
            if (!combo.getName().equals(vaccineComboUpdateRequest.getName())) {
                Combo checkExist = comboRepository.findComboByName(vaccineComboUpdateRequest.getName());
                if (checkExist != null) {
                    throw new ElementExistException("Gói tiêm đã tồn tại với tên là: " + vaccineComboUpdateRequest.getName());
                }
            }
            combo.setName(vaccineComboUpdateRequest.getName());
        }
        if (vaccineComboUpdateRequest.getDescription() != null) {
            combo.setDescription(vaccineComboUpdateRequest.getDescription());
        }
        if (vaccineComboUpdateRequest.getPrice() != null) {
            combo.setPrice(vaccineComboUpdateRequest.getPrice());
        }
        if (vaccineComboUpdateRequest.getMinAge() != null) {
            combo.setMinAge(vaccineComboUpdateRequest.getMinAge());
        }
        if (vaccineComboUpdateRequest.getMaxAge() != null) {
            combo.setMaxAge(vaccineComboUpdateRequest.getMaxAge());
        }

        if (vaccineComboUpdateRequest.getVaccines() != null && !vaccineComboUpdateRequest.getVaccines().isEmpty()) {

            List<VaccineCombo> updatedVaccineCombo = new ArrayList<>();

            VaccineComboResponseDTO previousVaccineCombo = null;

            int check = 1;

            for (VaccineComboResponseDTO vaccineComboResponseDTO : vaccineComboUpdateRequest.getVaccines()) {

                Vaccine vaccine = vaccineMapper.vaccineResponseDTOToVaccine(vaccineComboResponseDTO.getVaccine());

                VaccineComboId vaccineComboId = new VaccineComboId(vaccineComboResponseDTO.getId().getVaccineId(), comboID, vaccineComboResponseDTO.getId().getOrderInCombo());

                if (previousVaccineCombo != null) {

                    if (vaccine.equals(vaccineMapper.vaccineResponseDTOToVaccine(previousVaccineCombo.getVaccine()))) {
                        check += 1;
                        for (VaccineTiming vaccineTiming : vaccine.getVaccineTimings()) {
                            if (vaccineTiming.getDoseNo() == check) {
                                if (vaccineTiming.getIntervalDays() != vaccineComboResponseDTO.getIntervalDays()) {
                                    throw new BadRequestException(vaccine.getName() + " phải cách " + previousVaccineCombo.getVaccine().getName() + " ít nhất là " + vaccineTiming.getIntervalDays() + " ngày");
                                }
                            }
                        }
                    } else {
                        check = 1;
                        boolean isCurrentActivated = previousVaccineCombo.getVaccine().isActivated();
                        int requiredInterval = isCurrentActivated ? businessIntervalAfterActiveVaccine : businessIntervalAfterInactiveVaccine;
                        long actualInterval = vaccineComboResponseDTO.getIntervalDays();

                        if (actualInterval < requiredInterval) {
                            throw new BadRequestException(vaccine.getName() + " phải cách " + previousVaccineCombo.getVaccine().getName() + " ít nhất là " + requiredInterval + " ngày");
                        }
                    }
                }

                VaccineCombo vaccineCombo = VaccineCombo.builder()
                        .id(vaccineComboId)
                        .combo(combo)
                        .vaccine(vaccine)
                        .intervalDays(vaccineComboResponseDTO.getIntervalDays())
                        .build();
                updatedVaccineCombo.add(vaccineCombo);
                previousVaccineCombo = vaccineComboResponseDTO;
            }
            combo.setVaccineCombos(updatedVaccineCombo);
        }

        return comboMapper.comboToComboResponseDTO(comboRepository.save(combo));
    }

    @Override
    public ComboResponseDTO undeleteCombo(Integer comboID) {
        Combo combo = comboRepository.findComboById(comboID);
        if (combo == null) {
            throw new ElementNotFoundException("Không tìm thấy combo");
        }
        if (!combo.isDeleted()) {
            throw new UnchangedStateException("Combo chưa bị xóa");
        }
        combo.setDeleted(false);
        return comboMapper.comboToComboResponseDTO(comboRepository.save(combo));
    }

    @Override
    public ComboResponseDTO deleteCombo(Integer comboID) {
        Combo combo = comboRepository.findComboById(comboID);
        if (combo == null) {
            throw new ElementNotFoundException("Không tìm thấy combo");
        }
        combo.setDeleted(true);
        return comboMapper.comboToComboResponseDTO(comboRepository.save(combo));
    }
}
