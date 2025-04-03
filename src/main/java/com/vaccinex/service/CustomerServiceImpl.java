package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.CustomerUpdateProfile;
import com.sba301.vaccinex.dto.request.FeedbackRequestDTO;
import com.sba301.vaccinex.dto.request.ReactionCreateRequest;
import com.sba301.vaccinex.dto.response.ChildrenResponseDTO;
import com.sba301.vaccinex.dto.response.CustomerInfoResponse;
import com.sba301.vaccinex.dto.response.ReactionCreateResponse;
import com.sba301.vaccinex.exception.ElementNotFoundException;
import com.sba301.vaccinex.exception.EntityNotFoundException;
import com.sba301.vaccinex.mapper.AccountMapper;
import com.sba301.vaccinex.mapper.ChildrenMapper;
import com.sba301.vaccinex.pojo.Reaction;
import com.sba301.vaccinex.pojo.User;
import com.sba301.vaccinex.pojo.VaccineSchedule;
import com.sba301.vaccinex.repository.ChildrenRepository;
import com.sba301.vaccinex.repository.ReactionRepository;
import com.sba301.vaccinex.repository.UserRepository;
import com.sba301.vaccinex.repository.VaccineScheduleRepository;
import com.sba301.vaccinex.service.spec.CustomerService;
import jakarta.ejb.Stateless;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final UserRepository userRepository;
    private final VaccineScheduleRepository vaccineScheduleRepository;
    private final ReactionRepository reactionRepository;
    private final ChildrenRepository childrenRepository;

    @Override
    public List<ChildrenResponseDTO> getChildByParentId(Integer parentId) {
        return ChildrenMapper.INSTANCE.toDTOs(childrenRepository.findAllByGuardianIdAndDeletedIsFalse(parentId));
    }

    @Override
    public void updateFeedback(FeedbackRequestDTO feedbackRequestDTO, Integer scheduleId) {
        VaccineSchedule vaccineSchedule = vaccineScheduleRepository.findById(scheduleId).orElseThrow((() -> new ElementNotFoundException("Không tìm thấy lịch với ID: " + scheduleId)));
        vaccineSchedule.setFeedback(feedbackRequestDTO.getFeedback());
        vaccineScheduleRepository.save(vaccineSchedule);
    }

    @Override
    public ReactionCreateResponse createReactionDetail(ReactionCreateRequest reactionCreateRequest, Integer scheduleId) {
        VaccineSchedule vaccineSchedule = vaccineScheduleRepository.findById(scheduleId).orElseThrow(() -> new ElementNotFoundException("Không tìm thấy lịch với ID: " + scheduleId));
        Reaction reaction = Reaction.builder()
                .date(LocalDateTime.now())
                .reaction(reactionCreateRequest.reaction())
                .reportedBy(reactionCreateRequest.reportedBy())
                .schedule(vaccineSchedule)
                .build();
        reaction = reactionRepository.save(reaction);
        return ReactionCreateResponse.builder()
                .date(LocalDateTime.now())
                .id(reaction.getId())
                .reaction(reaction.getReaction())
                .reportedBy(reaction.getReportedBy())
                .build();
    }

    @Override
    public CustomerInfoResponse findUserById(Integer id) {
        return userRepository.findByIdAndDeletedIsFalse(id)
                .map(AccountMapper.INSTANCE::toCustomerInfoResponse)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
    }

    @Override
    public CustomerInfoResponse updateCustomer(Integer customerId, CustomerUpdateProfile request) {
        User user = userRepository.findByIdAndDeletedIsFalse(customerId).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());
        return AccountMapper.INSTANCE.toCustomerInfoResponse(userRepository.save(user));
    }

}
