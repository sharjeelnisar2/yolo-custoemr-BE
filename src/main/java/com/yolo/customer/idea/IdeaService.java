package com.yolo.customer.idea;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.customer.idea.dietaryRestriction.DietaryRestrictionRepository;
import com.yolo.customer.idea.ideaStatus.IdeaStatus;
import com.yolo.customer.idea.ideaStatus.IdeaStatusRepository;
import com.yolo.customer.idea.ideaStatus.IdeaStatusService;
import com.yolo.customer.idea.interest.Interest;
import com.yolo.customer.idea.dietaryRestriction.DietaryRestriction;
import com.yolo.customer.idea.interest.InterestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private IdeaStatusRepository ideaStatusRepository;

    @Autowired
    private IdeaStatusService ideaStatusService;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private DietaryRestrictionRepository dietaryRestrictionRepository;

//    @Autowired
//    private UserRepository userRepository;

    public ResponseEntity<Map<String, String>> submitIdeaToVendor(Integer ideaId, String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty.");
        }

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea with ID " + ideaId + " not found"));

        boolean vendorApiSuccess = callVendorApi(idea);

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

    private boolean callVendorApi(Idea idea) {

        IdeaDTO.IdeaDetails ideaDetails = new IdeaDTO.IdeaDetails();

//        User user = userRepository.findById(idea.getUserId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        ideaDetails.setCustomerName(user.getUsername());

        String dummyUsername = "Ahmad";
        ideaDetails.setCustomerName(dummyUsername);

        // Get idea details
        ideaDetails.setTitle(idea.getTitle());
        ideaDetails.setDescription(idea.getDescription());
        ideaDetails.setIdeaCode(idea.getCode());

        // Get interests
        List<String> interests = interestRepository.findByIdeaId(idea.getId())
                .stream()
                .map(Interest::getDescription)
                .collect(Collectors.toList());
        ideaDetails.setInterests(interests);

        // Get dietary restrictions
        List<String> dietaryRestrictions = dietaryRestrictionRepository.findByIdeaId(idea.getId())
                .stream()
                .map(DietaryRestriction::getDescription)
                .collect(Collectors.toList());
        ideaDetails.setDietaryRestrictions(dietaryRestrictions);

        IdeaDTO ideaDTO = new IdeaDTO();
        ideaDTO.setIdea(ideaDetails);

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        //HttpEntity<IdeaDTO> request = new HttpEntity<>(ideaDTO, headers);
        try {
            String requestBody = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(ideaDTO);
            System.out.println("Request body: " + requestBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // Handle exception as needed
        }
        return true;
    }

    @Transactional
    public Idea createDraftIdea(DraftIdeaRequest request, Long userId) {
        // Create Idea entity
        Idea idea = new Idea();
        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setUserId(1L);
        idea.setCode(generateUniqueCode());

        // Set initial status for the idea
        IdeaStatus draftStatus = ideaStatusRepository.findByValue("Draft").orElseThrow(() ->
                new RuntimeException("Default Idea Status not found"));
        idea.setIdeaStatus(draftStatus);

        // Save Idea
        idea = ideaRepository.save(idea);

        // Create and save Dietary Restrictions
        Idea finalIdea = idea;
        List<DietaryRestriction> restrictions = request.getDietaryRestrictions()
                .stream()
                .map(desc -> {
                    DietaryRestriction restriction = new DietaryRestriction();
                    restriction.setDescription(desc);
                    restriction.setIdea(finalIdea);
                    return restriction;
                })
                .collect(Collectors.toList());

        dietaryRestrictionRepository.saveAll(restrictions);

        // Create and save Interests
        Idea finalIdea1 = idea;
        List<Interest> interests = request.getInterests()
                .stream()
                .map(desc -> {
                    Interest interest = new Interest();
                    interest.setDescription(desc);
                    interest.setIdea(finalIdea1);
                    return interest;
                })
                .collect(Collectors.toList());

        interestRepository.saveAll(interests);

        return idea;
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}