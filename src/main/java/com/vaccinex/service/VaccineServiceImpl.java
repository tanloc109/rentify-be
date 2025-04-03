package com.vaccinex.service;

import com.vaccinex.base.exception.ElementExistException;
import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.UnchangedStateException;
import com.vaccinex.dao.VaccineDao;
import com.vaccinex.dao.VaccineIntervalDao;
import com.vaccinex.dao.VaccineTimingDao;
import com.vaccinex.dao.VaccineUseDao;
import com.vaccinex.dto.paging.PagingResponse;
import com.vaccinex.dto.request.VaccineCreateRequest;
import com.vaccinex.dto.request.VaccineTimingCreateRequest;
import com.vaccinex.dto.request.VaccineUpdateRequest;
import com.vaccinex.dto.response.VaccineDTO;
import com.vaccinex.dto.response.VaccineIntervalResponseDTO;
import com.vaccinex.dto.response.VaccineResponseDTO;
import com.vaccinex.dto.response.VaccineUseResponseDTO;
import com.vaccinex.mapper.VaccineMapper;
import com.vaccinex.pojo.Vaccine;
import com.vaccinex.pojo.VaccineInterval;
import com.vaccinex.pojo.VaccineTiming;
import com.vaccinex.pojo.VaccineUse;
import com.vaccinex.pojo.composite.VaccineIntervalId;
import com.vaccinex.utils.VaccineSpecification;
import jakarta.ejb.Stateless;
import jakarta.transaction.Transactional;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class VaccineServiceImpl extends BaseServiceImpl<Vaccine, Integer> implements VaccineService {

    private final VaccineDao vaccineRepository;
    private final VaccineDao vaccineUseRepository;
    private final VaccineMapper vaccineMapper;
    private final VaccineDao vaccineTimingRepository;
    private final VaccineDao vaccineIntervalRepository;

    @Value("${price.vaccine.below}")
    private String below;

    @Value("${price.vaccine.higher}")
    private String higher;

    @Value("${price.vaccine.avg-begin}")
    private String avgBegin;

    @Value("${price.vaccine.avg-end}")
    private String avgEnd;

    @Value("${price.vaccine.default}")
    private String priceDefault;

    @Value("${business.interval-after-active-vaccine}")
    private int businessIntervalAfterActiveVaccine;

    @Value("${business.interval-after-inactive-vaccine}")
    private int businessIntervalAfterInactiveVaccine;

    public VaccineServiceImpl(VaccineDao vaccineRepository, VaccineMapper vaccineMapper,
                              VaccineUseDao vaccineUseRepository,
                              VaccineTimingDao vaccineTimingRepository,
                              VaccineIntervalDao vaccineIntervalRepository) {
        super(vaccineRepository);
        this.vaccineRepository = vaccineRepository;
        this.vaccineMapper = vaccineMapper;
        this.vaccineUseRepository = vaccineUseRepository;
        this.vaccineTimingRepository = vaccineTimingRepository;
        this.vaccineIntervalRepository = vaccineIntervalRepository;
    }

    @Override
    public PagingResponse getAllVaccines(Integer currentPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = vaccineRepository.findAll(pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách vaccine với bộ lọc, sắp xếp và phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(vaccineMapper::vaccineToVaccineResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách vaccine với bộ lọc, sắp xếp và phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    public PagingResponse getAllVaccineActive(Integer currentPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(currentPage - 1, pageSize);

        var pageData = vaccineRepository.findAllByDeletedFalse(pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách vaccine đang hoạt động với phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(vaccineMapper::vaccineToVaccineResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách vaccine đang hoạt động với phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    @Transactional
    public VaccineResponseDTO createVaccine(VaccineCreateRequest vaccineCreateRequest) {
        Vaccine checkExist = vaccineRepository.findVaccineByVaccineCode(vaccineCreateRequest.getVaccineCode());
        if (checkExist != null) {
            throw new ElementExistException("Vaccine đã tồn tại với mã là: " + vaccineCreateRequest.getVaccineCode());
        }

        if (vaccineCreateRequest.getMinAge() > vaccineCreateRequest.getMaxAge()) {
            throw new BadRequestException("Tuổi nhỏ nhất phải nhỏ hơn tuổi lớn nhất");
        }

        Vaccine vaccine = Vaccine.builder()
                .name(vaccineCreateRequest.getName())
                .vaccineCode(vaccineCreateRequest.getVaccineCode())
                .manufacturer(vaccineCreateRequest.getManufacturer())
                .description(vaccineCreateRequest.getDescription())
                .price(vaccineCreateRequest.getPrice())
                .expiresInDays(vaccineCreateRequest.getExpiresInDays())
                .minAge(vaccineCreateRequest.getMinAge())
                .maxAge(vaccineCreateRequest.getMaxAge())
                .dose(vaccineCreateRequest.getDose())
                .activated(vaccineCreateRequest.isActivated())
                .build();

        vaccine = vaccineRepository.save(vaccine);

        List<Integer> vaccineUseIds = vaccineCreateRequest.getUses().stream()
                .map(VaccineUseResponseDTO::getId)
                .collect(Collectors.toList());

        List<VaccineUse> vaccineUses = vaccineUseRepository.findAllByIdInAndDeletedFalse(vaccineUseIds);

        if (vaccineUses.size() != vaccineUseIds.size()) {
            throw new ElementNotFoundException("Một số công dụng có thể đã bị xóa");
        }

        vaccine.setUses(vaccineUses);

        List<VaccineTiming> vaccineTimings = new ArrayList<>();

        if (vaccineCreateRequest.getVaccineTimings() != null) {

            for (VaccineTimingCreateRequest timingRequest : vaccineCreateRequest.getVaccineTimings()) {
                VaccineTiming timing = VaccineTiming.builder()
                        .doseNo(timingRequest.getDoseNo())
                        .intervalDays(timingRequest.getDaysAfterPreviousDose())
                        .vaccine(vaccine)
                        .build();
                vaccineTimings.add(timing);
            }
            vaccine.setVaccineTimings(vaccineTimings);
        }

        List<VaccineInterval> toVaccineIntervals = new ArrayList<>();

        if (vaccineCreateRequest.getToVaccineIntervals() != null) {
            for (VaccineIntervalResponseDTO interval : vaccineCreateRequest.getToVaccineIntervals()) {

                Vaccine toVaccine = vaccineRepository.findVaccineById(interval.getId().getToVaccineId());

                if (toVaccine == null) {
                    throw new ElementNotFoundException("Không tìm thấy Vaccine với ID " + interval.getId().getToVaccineId());
                }

                if (toVaccine.isActivated()) {
                    if (interval.getDaysBetween() < businessIntervalAfterActiveVaccine) {
                        throw new BadRequestException("Đối với loại vaccine sống phải cách ít nhất 30 ngày");
                    }
                } else {
                    if (interval.getDaysBetween() < businessIntervalAfterInactiveVaccine) {
                        throw new BadRequestException("Đối với loại vaccine bất hoạt phải cách ít nhất 7 ngày");
                    }
                }

                VaccineIntervalId vaccineIntervalId = VaccineIntervalId.builder()
                        .toVaccineId(interval.getId().getToVaccineId())
                        .fromVaccineId(vaccine.getId())
                        .build();

                VaccineInterval vaccineInterval = VaccineInterval.builder()
                        .id(vaccineIntervalId)
                        .toVaccine(toVaccine)
                        .fromVaccine(vaccine)
                        .daysBetween(interval.getDaysBetween())
                        .build();

                toVaccineIntervals.add(vaccineInterval);
            }

            vaccineIntervalRepository.saveAll(toVaccineIntervals);
            vaccine.setFromVaccineIntervals(toVaccineIntervals);
        } else {
            throw new BadRequestException("Vaccine phải rằng buộc với ít nhất 1 vaccine.");
        }

        return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
    }

    @Transactional
    @Override
    public VaccineResponseDTO updateVaccine(VaccineUpdateRequest vaccineUpdateRequest, int vaccineID) {
        Vaccine vaccine = vaccineRepository.findVaccineById(vaccineID);
        if (vaccine != null) {
            if (vaccineUpdateRequest.getName() != null) {
                vaccine.setName(vaccineUpdateRequest.getName());
            }
            if (vaccineUpdateRequest.getManufacturer() != null) {
                vaccine.setManufacturer(vaccineUpdateRequest.getManufacturer());
            }
            if (vaccineUpdateRequest.getDescription() != null) {
                vaccine.setDescription(vaccineUpdateRequest.getDescription());
            }
            if (vaccineUpdateRequest.getPrice() != null) {
                vaccine.setPrice(vaccineUpdateRequest.getPrice());
            }
            if (vaccineUpdateRequest.getExpiresInDays() != null) {
                vaccine.setExpiresInDays(vaccineUpdateRequest.getExpiresInDays());
            }
            if (vaccineUpdateRequest.getMinAge() != null) {
                vaccine.setMinAge(vaccineUpdateRequest.getMinAge());
            }
            if (vaccineUpdateRequest.getMaxAge() != null) {
                vaccine.setMaxAge(vaccineUpdateRequest.getMaxAge());
            }
            if (vaccineUpdateRequest.getDose() != null) {
                vaccine.setDose(vaccineUpdateRequest.getDose());
            }
            if (vaccineUpdateRequest.getActivated() != null) {
                vaccine.setActivated(vaccineUpdateRequest.getActivated());
            }
            if (vaccineUpdateRequest.getUses() != null && !vaccineUpdateRequest.getUses().isEmpty()) {

                List<Integer> vaccineUseIds = vaccineUpdateRequest.getUses().stream()
                        .map(VaccineUseResponseDTO::getId)
                        .collect(Collectors.toList());

                List<VaccineUse> vaccineUses = vaccineUseRepository.findAllById(vaccineUseIds);

                if (vaccineUses.size() != vaccineUseIds.size()) {
                    throw new ElementNotFoundException("Một số công dụng có thể đã bị xóa");
                }

                vaccine.setUses(vaccineUses);

            }
            if (vaccineUpdateRequest.getVaccineTimings() != null) {

                List<VaccineTiming> vaccineTimings = new ArrayList<>();

                if (vaccine.getVaccineTimings() != null) {
                    vaccineTimingRepository.deleteAll(vaccine.getVaccineTimings());
                    vaccine.getVaccineTimings().clear();
                }

                for (VaccineTimingCreateRequest timingRequest : vaccineUpdateRequest.getVaccineTimings()) {
                    VaccineTiming timing = VaccineTiming.builder()
                            .doseNo(timingRequest.getDoseNo())
                            .intervalDays(timingRequest.getDaysAfterPreviousDose())
                            .vaccine(vaccine)
                            .build();
                    vaccineTimings.add(timing);
                }
                vaccine.setVaccineTimings(vaccineTimings);
            }

            if (vaccineUpdateRequest.getToVaccineIntervals() != null) {

                List<VaccineInterval> toVaccineIntervals = new ArrayList<>();

                vaccineIntervalRepository.deleteByFromVaccineId(vaccine.getId());

                for (VaccineIntervalResponseDTO interval : vaccineUpdateRequest.getToVaccineIntervals()) {

                    Vaccine toVaccine = vaccineRepository.findVaccineById(interval.getId().getToVaccineId());

                    if (toVaccine == null) {
                        throw new ElementNotFoundException("Không tìm thấy Vaccine với ID " + interval.getId().getToVaccineId());
                    }

                    if (toVaccine.isActivated()) {
                        if (interval.getDaysBetween() < businessIntervalAfterActiveVaccine) {
                            throw new BadRequestException("Đối với loại vaccine sống phải cách ít nhất 30 ngày");
                        }
                    } else {
                        if (interval.getDaysBetween() < businessIntervalAfterInactiveVaccine) {
                            throw new BadRequestException("Đối với loại vaccine bất hoạt phải cách ít nhất 7 ngày");
                        }
                    }

                    VaccineIntervalId vaccineIntervalId = VaccineIntervalId.builder()
                            .toVaccineId(interval.getId().getToVaccineId())
                            .fromVaccineId(vaccine.getId())
                            .build();

                    VaccineInterval vaccineInterval = VaccineInterval.builder()
                            .id(vaccineIntervalId)
                            .toVaccine(toVaccine)
                            .fromVaccine(vaccine)
                            .daysBetween(interval.getDaysBetween())
                            .build();

                    toVaccineIntervals.add(vaccineInterval);
                }

                vaccineIntervalRepository.saveAll(toVaccineIntervals);
                vaccine.setFromVaccineIntervals(toVaccineIntervals);
            }

            return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
        }
        return null;
    }

    @Override
    public PagingResponse searchVaccines(Integer currentPage, Integer pageSize, String name, String purpose, String price, Integer minAge, Integer maxAge, String sortBy) {
        Pageable pageable;

        Specification<Vaccine> spec = Specification.where(null);

        List<String> keys = new ArrayList<>();
        List<String> values = new ArrayList<>();

        String searchName = "";
        if (StringUtils.hasText(name)) {
            searchName = name;
        }
        keys.add("name");
        values.add(searchName);

        String searchPurposeName = "";
        if (StringUtils.hasText(purpose)) {
            searchPurposeName = purpose;
        }
        keys.add("purpose");
        values.add(searchPurposeName);

        String searchPriceBegin = priceDefault;
        String searchPriceEnd = String.valueOf(vaccineRepository.getMaxPrice());
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
        String searchMaxAge = String.valueOf(vaccineRepository.getMaxAge());
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
                Specification<Vaccine> newSpec = VaccineSpecification.searchByField(field, value);
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

        var pageData = vaccineRepository.findAll(spec, pageable);

        return !pageData.getContent().isEmpty() ? PagingResponse.builder()
                .code("Success")
                .message("Lấy danh sách vaccine với bộ lọc, sắp xếp và phân trang thành công")
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalElements(pageData.getTotalElements())
                .totalPages(pageData.getTotalPages())
                .data(pageData.getContent().stream()
                        .map(vaccineMapper::vaccineToVaccineResponseDTO)
                        .toList())
                .build() :
                PagingResponse.builder()
                        .code("Failed")
                        .message("Lấy danh sách vaccine với bộ lọc, sắp xếp và phân trang không thành công")
                        .currentPage(currentPage)
                        .pageSize(pageSize)
                        .totalElements(pageData.getTotalElements())
                        .totalPages(pageData.getTotalPages())
                        .data(pageData.getContent().stream()
                                .map(vaccineMapper::vaccineToVaccineResponseDTO)
                                .toList())
                        .build();
    }

    @Override
    public VaccineResponseDTO undeleteVaccine(Integer vaccineID) {
        Vaccine vaccine = vaccineRepository.findVaccineById(vaccineID);
        if (vaccine == null) {
            throw new ElementNotFoundException("Không tìm thấy vaccine");
        }
        if (!vaccine.isDeleted()) {
            throw new UnchangedStateException("Vaccine chưa được xóa");
        }
        vaccine.setDeleted(false);
        return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
    }

    @Override
    public VaccineResponseDTO deleteVaccine(Integer vaccineID) {
        Vaccine vaccine = vaccineRepository.findVaccineById(vaccineID);
        if (vaccine == null) {
            throw new ElementNotFoundException("Không tìm thấy vaccine");
        }
        vaccine.setDeleted(true);
        return vaccineMapper.vaccineToVaccineResponseDTO(vaccineRepository.save(vaccine));
    }

    @Override
    public Vaccine getVaccineById(Integer vaccineID) {
        return vaccineRepository.findByIdAndDeletedIsFalse(vaccineID).orElseThrow(
                () -> new ElementNotFoundException("Không tìm thấy vaccine với ID: " + vaccineID)
        );
    }

    @Override
    public List<VaccineDTO> getVaccines() {
        List<Vaccine> vaccines = vaccineRepository.findByDeletedIsFalse();
        return vaccines.stream().map(
                v -> VaccineDTO.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .vaccineCode(v.getVaccineCode())
                        .build()
        ).toList();
    }

    @Override
    public List<VaccineResponseDTO> getVaccinesV2() {
        return vaccineRepository.findAll().stream().map(vaccineMapper::vaccineToVaccineResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<VaccineResponseDTO> getVaccinesActiveV2() {
        return vaccineRepository.findByDeletedIsFalse().stream().map(vaccineMapper::vaccineToVaccineResponseDTO).collect(Collectors.toList());
    }

}
