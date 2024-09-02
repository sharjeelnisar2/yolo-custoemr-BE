package com.yolo.customer.idea;

import com.yolo.customer.idea.dietaryRestriction.DietaryRestrictionRepository;
import com.yolo.customer.idea.dto.IdeaDTO;
import com.yolo.customer.idea.ideaStatus.IdeaStatus;
import com.yolo.customer.idea.ideaStatus.IdeaStatusRepository;
import com.yolo.customer.idea.ideaStatus.IdeaStatusService;
import com.yolo.customer.idea.interest.Interest;
import com.yolo.customer.idea.dietaryRestriction.DietaryRestriction;
import com.yolo.customer.idea.interest.InterestRepository;
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import com.yolo.customer.utils.GetContextHolder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final IdeaStatusRepository ideaStatusRepository;
    private final IdeaStatusService ideaStatusService;
    private final InterestRepository interestRepository;
    private final DietaryRestrictionRepository dietaryRestrictionRepository;
    private final UserRepository userRepository;

    @Autowired
    public IdeaService(IdeaRepository ideaRepository,
                                IdeaStatusRepository ideaStatusRepository,
                                IdeaStatusService ideaStatusService,
                                InterestRepository interestRepository,
                                DietaryRestrictionRepository dietaryRestrictionRepository,
                                UserRepository userRepository) {
        this.ideaRepository = ideaRepository;
        this.ideaStatusRepository = ideaStatusRepository;
        this.ideaStatusService = ideaStatusService;
        this.interestRepository = interestRepository;
        this.dietaryRestrictionRepository = dietaryRestrictionRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Map<String, String>> submitIdeaToVendor(Integer ideaId, String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = GetContextHolder.getUsernameFromAuthentication(authentication);
        User loggedInUser = userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User with given username does not exist"));

        if (status == null || status.isEmpty()) {
            throw new EntityNotFoundException("Status cannot be empty.");
        }

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new EntityNotFoundException("Ideas not found"));

        if (!idea.getUserId().equals(loggedInUser.getId())) {
            throw new RuntimeException("Unauthorized to update idea.");
        }

        boolean vendorApiSuccess = callVendorApi(idea, username);

        if (vendorApiSuccess) {
            Long statusId = ideaStatusService.findStatusIdByName(status);

            IdeaStatus ideaStatus = new IdeaStatus();
            ideaStatus.setId(statusId);
            idea.setIdeaStatus(ideaStatus);
            ideaRepository.save(idea);

            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Idea submitted and status updated successfully.");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            throw new RuntimeException("Failed to submit idea to the vendor.");
        }
    }


    private boolean callVendorApi(Idea idea, String username) {
        IdeaDTO.IdeaDetails ideaDetails = new IdeaDTO.IdeaDetails();
        ideaDetails.setCustomer_name(username);
        ideaDetails.setTitle(idea.getTitle());
        ideaDetails.setDescription(idea.getDescription());
        ideaDetails.setIdea_code(idea.getCode());

        List<String> interests = interestRepository.findByIdeaId(idea.getId())
                .stream()
                .map(Interest::getDescription)
                .collect(Collectors.toList());
        ideaDetails.setInterests(interests);

        List<String> dietaryRestrictions = dietaryRestrictionRepository.findByIdeaId(idea.getId())
                .stream()
                .map(DietaryRestriction::getDescription)
                .collect(Collectors.toList());
        ideaDetails.setDietary_restrictions(dietaryRestrictions);

        IdeaDTO ideaDTO = new IdeaDTO();
        ideaDTO.setIdea(ideaDetails);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<IdeaDTO> requestEntity = new HttpEntity<>(ideaDTO, headers);

        String vendorApiUrl = "http://localhost:8081/api/v1/ideas";

        RestTemplate restTemplate = new RestTemplate();

        return true;
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}