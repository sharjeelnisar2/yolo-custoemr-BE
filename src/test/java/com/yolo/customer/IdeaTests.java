//package com.yolo.customer;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yolo.customer.idea.Idea;
//import com.yolo.customer.idea.IdeaController;
//import com.yolo.customer.idea.IdeaService;
//import com.yolo.customer.idea.dto.IdeaRequest;
//import com.yolo.customer.idea.dto.IdeaResponse;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.mockito.Mockito.when;
//
//@WebMvcTest(IdeaController.class)
//public class IdeaTests {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private IdeaService ideaService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @DisplayName("POST /users/ideas/create-draft - Should create draft idea successfully when user has CREATE_IDEA authority")
//    @WithMockUser(username = "creator", authorities = {"CREATE_IDEA"})
//    void testCreateDraftIdea_Success() throws Exception {
//        // Prepare mock IdeaRequest
//        IdeaRequest ideaRequest = new IdeaRequest();
//        ideaRequest.setTitle("Innovative Idea");
//        ideaRequest.setDescription("Description of innovative idea");
//        ideaRequest.setInterests(Arrays.asList("Cooking", "Reading"));
//        ideaRequest.setDietaryRestrictions(Arrays.asList("Vegan"));
//
//        // Prepare mock Idea object to be returned by service
//        Idea mockIdea = new Idea();
//        mockIdea.setId(1);
//        mockIdea.setTitle(ideaRequest.getTitle());
//        mockIdea.setDescription(ideaRequest.getDescription());
//        mockIdea.setUserId(100);
//        mockIdea.setCode("ABC12345");
//        mockIdea.setCreatedAt(LocalDateTime.now());
//
//        // Mock the service layer
//        when(ideaService.createDraftIdea(any(IdeaRequest.class))).thenReturn(mockIdea);
//
//        // Perform POST request
//        mockMvc.perform(post("/users/ideas/create-draft")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ideaRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.idea.id").value(mockIdea.getId()))
//                .andExpect(jsonPath("$.idea.title").value(mockIdea.getTitle()))
//                .andExpect(jsonPath("$.idea.description").value(mockIdea.getDescription()))
//                .andExpect(jsonPath("$.idea.code").value(mockIdea.getCode()));
//
//        // Verify service was called
//        verify(ideaService, times(1)).createDraftIdea(any(IdeaRequest.class));
//    }
//
//    @Test
//    @DisplayName("POST /users/ideas/create-draft - Should return 400 Bad Request when required fields are missing")
//    @WithMockUser(username = "creator", authorities = {"CREATE_IDEA"})
//    void testCreateDraftIdea_MissingFields() throws Exception {
//        // Prepare mock IdeaRequest with missing title and interests
//        IdeaRequest ideaRequest = new IdeaRequest();
//        ideaRequest.setDescription("Description without title and interests");
//
//        // Perform POST request
//        mockMvc.perform(post("/users/ideas/create-draft")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ideaRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").exists());
//
//        // Verify service was not called
//        verify(ideaService, times(0)).createDraftIdea(any(IdeaRequest.class));
//    }
//
//    @Test
//    @DisplayName("POST /users/ideas/create-draft - Should return 400 Bad Request when interests are missing")
//    @WithMockUser(username = "creator", authorities = {"CREATE_IDEA"})
//    void testCreateDraftIdea_MissingInterests() throws Exception {
//        // Prepare mock IdeaRequest with missing interests
//        IdeaRequest ideaRequest = new IdeaRequest();
//        ideaRequest.setTitle("Title without interests");
//        ideaRequest.setDescription("Description without interests");
//
//        // Perform POST request
//        mockMvc.perform(post("/users/ideas/create-draft")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ideaRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("At least one interest should be entered"));
//
//        // Verify service was not called
//        verify(ideaService, times(0)).createDraftIdea(any(IdeaRequest.class));
//    }
//
//    @Test
//    @DisplayName("POST /users/ideas/create-draft - Should return 403 Forbidden when user lacks CREATE_IDEA authority")
//    @WithMockUser(username = "user", authorities = {"VIEW_ALL_IDEAS"})
//    void testCreateDraftIdea_Unauthorized() throws Exception {
//        // Prepare mock IdeaRequest
//        IdeaRequest ideaRequest = new IdeaRequest();
//        ideaRequest.setTitle("Unauthorized Idea");
//        ideaRequest.setDescription("Should not be created");
//        ideaRequest.setInterests(Arrays.asList("Cooking"));
//
//        // Perform POST request
//        mockMvc.perform(post("/users/ideas/create-draft")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ideaRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isForbidden());
//
//        // Verify service was not called
//        verify(ideaService, times(0)).createDraftIdea(any(IdeaRequest.class));
//    }
//
//    @Test
//    @DisplayName("POST /users/ideas/create-draft - Should return 401 Unauthorized when user is not authenticated")
//    void testCreateDraftIdea_NotAuthenticated() throws Exception {
//        // Prepare mock IdeaRequest
//        IdeaRequest ideaRequest = new IdeaRequest();
//        ideaRequest.setTitle("Unauthenticated Idea");
//        ideaRequest.setDescription("Should not be created");
//        ideaRequest.setInterests(Arrays.asList("Cooking"));
//
//        // Perform POST request without authentication
//        mockMvc.perform(post("/users/ideas/create-draft")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ideaRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isUnauthorized());
//
//        // Verify service was not called
//        verify(ideaService, times(0)).createDraftIdea(any(IdeaRequest.class));
//    }
//
//    @Test
//    @DisplayName("POST /users/ideas/create-draft - Should handle service layer exceptions gracefully")
//    @WithMockUser(username = "creator", authorities = {"CREATE_IDEA"})
//    void testCreateDraftIdea_ServiceException() throws Exception {
//        // Prepare mock IdeaRequest
//        IdeaRequest ideaRequest = new IdeaRequest();
//        ideaRequest.setTitle("Idea with Exception");
//        ideaRequest.setDescription("This should trigger an exception");
//        ideaRequest.setInterests(Arrays.asList("Cooking"));
//
//        // Mock the service to throw an exception
//        when(ideaService.createDraftIdea(any(IdeaRequest.class)))
//                .thenThrow(new IllegalArgumentException("Invalid idea data"));
//
//        // Perform POST request
//        mockMvc.perform(post("/users/ideas/create-draft")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(ideaRequest))
//                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Invalid idea data"));
//
//        // Verify service was called
//        verify(ideaService, times(1)).createDraftIdea(any(IdeaRequest.class));
//    }
//
//    @Test
//    @DisplayName("GET /users/ideas - Should retrieve ideas successfully when user has VIEW_ALL_IDEAS authority")
//    @WithMockUser(username = "viewer", authorities = {"VIEW_ALL_IDEAS"})
//    void testGetIdeas_Success() throws Exception {
//        // Prepare mock IdeaResponse
//        IdeaResponse idea1 = new IdeaResponse();
//        idea1.setIdeaId(1);
//        idea1.setTitle("Idea One");
//        idea1.setDescription("Description One");
//        idea1.setInterests(Arrays.asList("Cooking", "Reading"));
//        idea1.setDietaryRestrictions(Arrays.asList("Vegan"));
//        idea1.setIdeaStatus("DRAFT");
//        idea1.setCreatedAt(LocalDateTime.now());
//
//        IdeaResponse idea2 = new IdeaResponse();
//        idea2.setIdeaId(2);
//        idea2.setTitle("Idea Two");
//        idea2.setDescription("Description Two");
//        idea2.setInterests(Arrays.asList("Swimming"));
//        idea2.setDietaryRestrictions(Arrays.asList("Gluten-Free"));
//        idea2.setIdeaStatus("DRAFT");
//        idea2.setCreatedAt(LocalDateTime.now());
//
//        List<IdeaResponse> ideas = Arrays.asList(idea1, idea2);
//        // Mock the service layer
//        when(ideaService.getIdeas(any(Optional.class), any(String.class), anyInt(), anyInt(), any(String.class)))
//                .thenReturn((Page) ideas);
//
//        // Perform GET request
//        mockMvc.perform(get("/users/ideas")
//                        .param("page", "0")
//                        .param("size", "2")
//                        .param("sort", "createdAt,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content").isArray())
//                .andExpect(jsonPath("$.content.length()").value(ideas.size()))
//                .andExpect(jsonPath("$.content[0].ideaId").value(idea1.getIdeaId()))
//                .andExpect(jsonPath("$.content[1].ideaId").value(idea2.getIdeaId()));
//
//        // Verify service was called
//        verify(ideaService, times(1)).getIdeas(any(Optional.class), any(String.class), anyInt(), anyInt(), any(String.class));
//    }
//
//    @Test
//    @DisplayName("GET /users/ideas - Should return 403 Forbidden when user lacks VIEW_ALL_IDEAS authority")
//    @WithMockUser(username = "user", authorities = {"CREATE_IDEA"})
//    void testGetIdeas_Unauthorized() throws Exception {
//        // Perform GET request
//        mockMvc.perform(get("/users/ideas")
//                        .param("page", "0")
//                        .param("size", "2")
//                        .param("sort", "createdAt,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isForbidden());
//
//        // Verify service was not called
//        verify(ideaService, times(0)).getIdeas(any(Optional.class), any(String.class), anyInt(), anyInt(), any(String.class));
//    }
//
//    @Test
//    @DisplayName("GET /users/ideas - Should return 401 Unauthorized when user is not authenticated")
//    void testGetIdeas_NotAuthenticated() throws Exception {
//        // Perform GET request without authentication
//        mockMvc.perform(get("/users/ideas")
//                        .param("page", "0")
//                        .param("size", "2")
//                        .param("sort", "createdAt,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized());
//
//        // Verify service was not called
//        verify(ideaService, times(0)).getIdeas(any(Optional.class), any(String.class), anyInt(), anyInt(), any(String.class));
//    }
//
//    @Test
//    @DisplayName("GET /users/ideas - Should handle service layer exceptions gracefully")
//    @WithMockUser(username = "viewer", authorities = {"VIEW_ALL_IDEAS"})
//    void testGetIdeas_ServiceException() throws Exception {
//        // Mock the service to throw an exception
//        when(ideaService.getIdeas(any(Optional.class), any(String.class), anyInt(), anyInt(), any(String.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        // Perform GET request
//        mockMvc.perform(get("/users/ideas")
//                        .param("page", "0")
//                        .param("size", "2")
//                        .param("sort", "createdAt,desc")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Unexpected error"));
//
//        // Verify service was called
//        verify(ideaService, times(1)).getIdeas(any(Optional.class), any(String.class), anyInt(), anyInt(), any(String.class));
//    }
//}
