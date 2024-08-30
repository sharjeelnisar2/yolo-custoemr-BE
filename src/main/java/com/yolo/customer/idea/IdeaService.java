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
import com.yolo.customer.user.User;
import com.yolo.customer.user.UserRepository;
import com.yolo.customer.utils.GetContextHolder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;

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

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private UserRepository userRepository;

    public ResponseEntity<Map<String, String>> submitIdeaToVendor(Integer ideaId, String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = GetContextHolder.getUsernameFromAuthentication(authentication);
        User loggedInUser = userRepository.findByUsername(username).orElseThrow(() ->
                new EntityNotFoundException("User with given username does not exist: " + username));

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

        // Get interests
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

        // Prepare headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create HttpEntity
        HttpEntity<IdeaDTO> requestEntity = new HttpEntity<>(ideaDTO, headers);

        // Vendor API URL (Replace with actual URL and port)
        String vendorApiUrl = "http://localhost:8081/api/v1/ideas";

        // Create RestTemplate
        RestTemplate restTemplate = new RestTemplate();

//        try {
//            // Send POST request
//            ResponseEntity<String> response = restTemplate.exchange(
//                    vendorApiUrl,
//                    HttpMethod.POST,
//                    requestEntity,
//                    String.class
//            );
//
//            // Check response status
//            if (response.getStatusCode() == HttpStatus.CREATED) {
//                return true; // Success
//            } else {
//                System.out.println("Unexpected response status: " + response.getStatusCode());
//                return false; // Failed
//            }
//        } catch (Exception e) {
//            e.printStackTrace(); // Handle exception as needed
//            return false; // Failed
//        }

        return true;
    }


//    public static String getCurrentUserId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
//            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
//            return userDetails.getUsername(); // Assuming username is the user ID
//        }
//        return null; // or throw an exception if preferred
//    }

    @Transactional
    public Idea createDraftIdea(DraftIdeaRequest request) {
        // Create Idea entity
        Idea idea = new Idea();
        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setUserId(1); // Replace this with actual user ID
        idea.setCode(generateUniqueCode());

        // Set initial status for the idea
        IdeaStatus draftStatus = ideaStatusRepository.findByValue("Draft")
                .orElseThrow(() -> new RuntimeException("Default Idea Status not found"));
        idea.setIdeaStatus(draftStatus);

        // Save Idea
        idea = ideaRepository.save(idea);

        // Create and save Dietary Restrictions if they are not empty
        List<String> dietaryRestrictions = request.getDietaryRestrictions();
        if (dietaryRestrictions != null && !dietaryRestrictions.isEmpty()) {
            Idea finalIdea = idea;
            List<DietaryRestriction> restrictions = dietaryRestrictions.stream()
                    .filter(desc -> desc != null && !desc.trim().isEmpty())  // Filter out empty or null values
                    .limit(3)  // Limit to 3 dietary restrictions
                    .map(desc -> {
                        DietaryRestriction restriction = new DietaryRestriction();
                        restriction.setDescription(desc);
                        restriction.setIdea(finalIdea);
                        return restriction;
                    })
                    .collect(Collectors.toList());

            if (!restrictions.isEmpty()) { // Save only if there are valid restrictions
                dietaryRestrictionRepository.saveAll(restrictions);
            }
        }

        // Create and save Interests if they are not empty
        List<String> interestsList = request.getInterests();
        if (interestsList != null && !interestsList.isEmpty()) {
            Idea finalIdea1 = idea;
            List<Interest> interests = interestsList.stream()
                    .filter(desc -> desc != null && !desc.trim().isEmpty())  // Filter out empty or null values
                    .limit(3)  // Limit to 3 interests
                    .map(desc -> {
                        Interest interest = new Interest();
                        interest.setDescription(desc);
                        interest.setIdea(finalIdea1);
                        return interest;
                    })
                    .collect(Collectors.toList());

            if (!interests.isEmpty()) { // Save only if there are valid interests
                interestRepository.saveAll(interests);
            }
        }

        return idea;
    }



    private String generateUniqueCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}