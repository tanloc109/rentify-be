package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.CustomerUpdateProfile;
import com.sba301.vaccinex.dto.request.FeedbackRequestDTO;
import com.sba301.vaccinex.dto.request.ReactionCreateRequest;
import com.sba301.vaccinex.dto.response.ChildrenResponseDTO;
import com.sba301.vaccinex.dto.response.CustomerInfoResponse;
import com.sba301.vaccinex.dto.response.ReactionCreateResponse;

import java.util.List;

public interface CustomerService {
    List<ChildrenResponseDTO> getChildByParentId(Integer parentId);

    void updateFeedback(FeedbackRequestDTO feedbackRequestDTO, Integer scheduleId);

    ReactionCreateResponse createReactionDetail(ReactionCreateRequest reactionCreateRequest, Integer scheduleId);

    CustomerInfoResponse findUserById(Integer id);

    CustomerInfoResponse updateCustomer(Integer customerId, CustomerUpdateProfile request);
}
