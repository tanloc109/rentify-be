package com.vaccinex.service;

import com.vaccinex.dto.request.CustomerUpdateProfile;
import com.vaccinex.dto.request.FeedbackRequestDTO;
import com.vaccinex.dto.request.ReactionCreateRequest;
import com.vaccinex.dto.response.ChildrenResponseDTO;
import com.vaccinex.dto.response.CustomerInfoResponse;
import com.vaccinex.dto.response.ReactionCreateResponse;

import java.util.List;

public interface CustomerService {
    List<ChildrenResponseDTO> getChildByParentId(Integer parentId);

    void updateFeedback(FeedbackRequestDTO feedbackRequestDTO, Integer scheduleId);

    ReactionCreateResponse createReactionDetail(ReactionCreateRequest reactionCreateRequest, Integer scheduleId);

    CustomerInfoResponse findUserById(Integer id);

    CustomerInfoResponse updateCustomer(Integer customerId, CustomerUpdateProfile request);
}
