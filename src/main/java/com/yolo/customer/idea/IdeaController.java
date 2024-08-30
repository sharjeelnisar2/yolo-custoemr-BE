package com.yolo.customer.idea;

import com.yolo.customer.idea.dto.DraftIdeaRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/users/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }


    @PreAuthorize("hasAuthority('ROLE_UPDATE_IDEA_STATUS')")
    @PatchMapping("/{ideaId}")
    public ResponseEntity<Map<String, String>> submitIdeaToVendor(@PathVariable("ideaId") Integer ideaId, @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");
        return ideaService.submitIdeaToVendor(ideaId, status);
    }

    @PostMapping("/draft")
    public ResponseEntity<Idea> createDraftIdea(@RequestBody DraftIdeaRequest request) {
        Idea idea = ideaService.createDraftIdea(request);
        return ResponseEntity.ok(idea);
    }

}