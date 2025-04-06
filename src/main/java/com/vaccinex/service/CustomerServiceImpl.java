package com.vaccinex.service;

import com.vaccinex.base.exception.ElementNotFoundException;
import com.vaccinex.base.exception.IdNotFoundException;
import com.vaccinex.dao.ChildrenDao;
import com.vaccinex.dao.ReactionDao;
import com.vaccinex.dao.UserDao;
import com.vaccinex.dao.VaccineScheduleDao;
import com.vaccinex.dto.request.CustomerUpdateProfile;
import com.vaccinex.dto.request.FeedbackRequestDTO;
import com.vaccinex.dto.request.ReactionCreateRequest;
import com.vaccinex.dto.response.ChildrenResponseDTO;
import com.vaccinex.dto.response.CustomerInfoResponse;
import com.vaccinex.dto.response.ReactionCreateResponse;
import com.vaccinex.mapper.AccountMapper;
import com.vaccinex.mapper.ChildrenMapper;
import com.vaccinex.pojo.Reaction;
import com.vaccinex.pojo.User;
import com.vaccinex.pojo.VaccineSchedule;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Stateless
public class CustomerServiceImpl implements CustomerService {

    @Inject
    private UserDao userRepository;

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    @Inject
    private ReactionDao reactionRepository;

    @Inject
    private ChildrenDao childrenRepository;

    @Override
    public List<ChildrenResponseDTO> getChildByParentId(Integer parentId) {
        return ChildrenMapper.INSTANCE.toDTOs(childrenRepository.findAllByGuardianIdAndDeletedIsFalse(parentId));
    }

    @Override
    public void updateFeedback(FeedbackRequestDTO feedbackRequestDTO, Integer scheduleId) {
        VaccineSchedule vaccineSchedule = vaccineScheduleRepository.findById(scheduleId).orElseThrow((() -> new ElementNotFoundException("Schedule not found with ID: " + scheduleId)));
        vaccineSchedule.setFeedback(feedbackRequestDTO.getFeedback());
        vaccineScheduleRepository.save(vaccineSchedule);
    }

    @Override
    public ReactionCreateResponse createReactionDetail(ReactionCreateRequest reactionCreateRequest, Integer scheduleId) {
       VaccineSchedule vaccineSchedule = vaccineScheduleRepository.findById(scheduleId).orElseThrow(() -> new ElementNotFoundException("Schedule not found with ID: " + scheduleId));
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
                .orElseThrow(() -> new IdNotFoundException("Customer not found"));
    }

    @Override
    public CustomerInfoResponse updateCustomer(Integer customerId, CustomerUpdateProfile request) {
        User user = userRepository.findByIdAndDeletedIsFalse(customerId).orElseThrow(() -> new IdNotFoundException("Customer not found"));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setEmail(request.getEmail());
        user.setDob(request.getDob());
        return AccountMapper.INSTANCE.toCustomerInfoResponse(userRepository.save(user));
    }

}
