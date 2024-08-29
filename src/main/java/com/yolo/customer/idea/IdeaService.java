package com.yolo.customer.idea;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yolo.customer.idea.dietaryRestriction.DietaryRestriction;
import com.yolo.customer.idea.dietaryRestriction.DietaryRestrictionRepository;
import com.yolo.customer.idea.ideaStatus.IdeaStatus;
import com.yolo.customer.idea.ideaStatus.IdeaStatusRepository;
import com.yolo.customer.idea.ideaStatus.IdeaStatusService;
import com.yolo.customer.idea.interest.Interest;
import com.yolo.customer.idea.interest.InterestRepository;
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IdeaService {

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private IdeaStatusRepository ideaStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IdeaStatusService ideaStatusService;

    @Autowired
    private InterestRepository interestRepository;

    @Autowired
    private DietaryRestrictionRepository dietaryRestrictionRepository;

    public ResponseEntity<Map<String, String>> submitIdeaToVendor(Integer ideaId, String status) {
        if (status == null || status.isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty.");
        }

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new RuntimeException("Idea with ID " + ideaId + " not found"));

        boolean vendorApiSuccess = callVendorApi(idea);

        if (vendorApiSuccess) {
            IdeaStatus ideaStatus = ideaStatusRepository.findByValue(status)
                    .orElseThrow(() -> new RuntimeException("Idea status with name " + status + " not found"));

            idea.setIdeaStatus(ideaStatus); // Set IdeaStatus entity directly
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

        // Print request body (for debugging)
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
    public Idea createDraftIdea(IdeaRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User loggedInUser = userRepository.findByUsername(username);

        List<String> interestsList = request.getInterests();
        if (interestsList == null || interestsList.isEmpty()) {
            throw new IllegalArgumentException("At least one interest should be entered");
        }

        Idea idea = new Idea();
        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setUserId(1); // Replace with actual user ID if needed
        idea.setCode(generateUniqueCode());

        IdeaStatus draftStatus = ideaStatusRepository.findByValue("Draft")
                .orElseThrow(() -> new RuntimeException("Default Idea Status not found"));
        idea.setIdeaStatus(draftStatus); // Set IdeaStatus entity directly

        idea = ideaRepository.save(idea);

        List<String> dietaryRestrictions = request.getDietaryRestrictions();
        if (dietaryRestrictions != null && !dietaryRestrictions.isEmpty()) {
            Idea finalIdea = idea;
            List<DietaryRestriction> restrictions = dietaryRestrictions.stream()
                    .filter(desc -> desc != null && !desc.trim().isEmpty())
                    .limit(3)
                    .map(desc -> {
                        DietaryRestriction restriction = new DietaryRestriction();
                        restriction.setDescription(desc);
                        restriction.setIdea(finalIdea);
                        return restriction;
                    })
                    .collect(Collectors.toList());

            if (!restrictions.isEmpty()) {
                dietaryRestrictionRepository.saveAll(restrictions);
            }
        }

        Idea finalIdea1 = idea;
        List<Interest> interests = interestsList.stream()
                .filter(desc -> desc != null && !desc.trim().isEmpty())
                .limit(3)
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

    @Transactional
    public Page<IdeaResponse> getIdeas(Optional<Integer> statusId, String search, int page, int size, String sortOrder) {
        Pageable pageable = PageRequest.of(page - 1, size,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending());

        Page<Idea> ideas;

        // Check if the status exists
        if (statusId.isPresent() && ideaStatusRepository.existsById(statusId.get())) {
            if (search != null && !search.isEmpty()) {
                ideas = ideaRepository.findByIdeaStatusIdAndTitleContainingIgnoreCase(statusId.get(), search, pageable);
            } else {
                ideas = ideaRepository.findByIdeaStatusId(statusId.get(), pageable);
            }
        } else if (search != null && !search.isEmpty()) {
            ideas = ideaRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else {
            ideas = ideaRepository.findAll(pageable);
        }

        // Map to IdeaResponse and include Interests and DietaryRestrictions
        return ideas.map(this::mapToIdeaResponse);
    }

    private IdeaResponse mapToIdeaResponse(Idea idea) {
        List<String> interests = interestRepository.findByIdeaId(idea.getId())
                .stream()
                .map(Interest::getDescription)
                .collect(Collectors.toList());

        List<String> dietaryRestrictions = dietaryRestrictionRepository.findByIdeaId(idea.getId())
                .stream()
                .map(DietaryRestriction::getDescription)
                .collect(Collectors.toList());

        IdeaResponse response = new IdeaResponse();
        response.setIdeaId(idea.getId());
        response.setTitle(idea.getTitle());
        response.setDescription(idea.getDescription());
        response.setInterests(interests);
        response.setDietaryRestrictions(dietaryRestrictions);
        response.setIdeaStatus(ideaStatusRepository.findById(idea.getIdeaStatus().getId())
                .map(IdeaStatus::getCode) // Adjust this to your actual method
                .orElse(null)); // Use IdeaStatus entity directly
        response.setCreatedAt(idea.getCreatedAt());

        return response;
    }

    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
